package home.balda.microblog;

/**
 * Created by katia on 30/04/2018.
 */


import home.balda.microblog.entity.Post;
import home.balda.microblog.representation.PostRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.*;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.*;

import static org.elasticsearch.index.query.QueryBuilders.matchAllQuery;

@Component
public class BlogService {
    @Autowired
    Environment env;
    @Autowired
    ElasticsearchTemplate elasticsearchTemplate;

    public List<Post> getPosts() {
        SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(matchAllQuery()).build();
        List<Post> posts = elasticsearchTemplate.queryForList(searchQuery, Post.class);
        return posts;
    }

    public Post getPost(String id) {
        GetQuery getQuery = new GetQuery();
        getQuery.setId(id);
        return elasticsearchTemplate.queryForObject(getQuery, Post.class);
    }

    public String updatePost(String id, PostRequest post) {
        validateParameter(Post.TEXT, post.getText());
        IndexRequest indexRequest = new IndexRequest();
        Map<String, Object> updateFields = new HashMap<>();
        updateFields.put(Post.TEXT, post.getText());
        updateFields.put(Post.LAST_MODIFIED, new SimpleDateFormat(BlogConstants.DATE_FORMAT).format(new Date()));
        indexRequest.source(updateFields);

        UpdateQuery updateQuery = new UpdateQueryBuilder().withId(id)
                .withClass(Post.class).withIndexRequest(indexRequest).build();
        return elasticsearchTemplate.update(updateQuery).getId();

    }

    public String deletePost(String id) {
        return elasticsearchTemplate.delete(Post.class, id);
    }

    public String addPost(PostRequest post, String creator) {
        Post document = new Post(validateParameter(Post.TITLE, post.getTitle()), validateParameter(Post.TEXT, post.getText()));
        Date date = new Date();
        document.setRating(0);
        document.setCreator(creator);
        document.setCreated(date);
        document.setUpvoters(new ArrayList<>());
        document.setDownvoters(new ArrayList<>());
        document.setLastModified(date);

        IndexQuery indexQuery = new IndexQueryBuilder().withObject(document).build();
        return elasticsearchTemplate.index(indexQuery);

    }

    public Post echo(Post post) {
        return post;
    }

    public void upvote(String id, String username) throws BlogException {
        vote(id, username, true);
    }

    public void downvote(String id, String username) throws BlogException {
        vote(id, username, false);
    }

    public List<Post> getTopPosts(Optional<Integer> count) {
        int defaultCount = Integer.parseInt(env.getProperty("blog.top_posts_default_count"));

        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(matchAllQuery())
                .withSort(SortBuilders.fieldSort(Post.RATING).order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort(Post.CREATED).order(SortOrder.DESC))
                .withPageable(PageRequest.of(0, count.orElse(defaultCount)))
                .build();
        Page<Post> posts = elasticsearchTemplate.queryForPage(searchQuery, Post.class);
        return posts.getContent();
    }

    public void deletePosts() {
        elasticsearchTemplate.deleteIndex(Post.class);
        elasticsearchTemplate.createIndex(Post.class);
        elasticsearchTemplate.putMapping(Post.class);
        elasticsearchTemplate.refresh(Post.class);
    }

    private void vote(String id, String username, boolean isUpvote) throws BlogException {
        GetQuery getQuery = new GetQuery();
        getQuery.setId(id);
        Post post = elasticsearchTemplate.queryForObject(getQuery, Post.class);
        if (post.getUpvoters().contains(username) || post.getDownvoters().contains(username))
            throw new BlogException(BlogException.ALREADY_VOTED_MESSAGE, username);

        IndexRequest indexRequest = new IndexRequest();
        Map<String, Object> updateFields = new HashMap<>();

        if (isUpvote) {
            post.addUpVoter(username);
            updateFields.put(Post.UPVOTERS, post.getUpvoters());
        } else {
            post.addDownVoter(username);
            updateFields.put(Post.DOWNVOTERS, post.getDownvoters());
        }
        updateFields.put("rating", post.getRating());
        indexRequest.source(updateFields);

        UpdateQuery updateQuery = new UpdateQueryBuilder().withId(post.getId())
                .withClass(Post.class).withIndexRequest(indexRequest).build();
        elasticsearchTemplate.update(updateQuery);
    }


    private String validateParameter(String parameter, String value) {
        if (value == null || value.isEmpty())
            throw new BlogException(BlogException.MISSING_MANDATORY_PARAMETER, parameter);
        return value;
    }

}
