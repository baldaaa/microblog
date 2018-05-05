package home.balda.microblog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import static org.junit.Assert.assertNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import home.balda.microblog.entity.Post;
import home.balda.microblog.entity.PostId;
import home.balda.microblog.representation.BlogErrorResponse;
import home.balda.microblog.representation.PostRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class BlogControllerTest {

    @Autowired
    private MockMvc mvc;

    private Gson gson;

    @Before
    public void setUp() throws Exception {
        gson = new GsonBuilder().setDateFormat(BlogConstants.DATE_FORMAT).create();
    }

    @Test
    public void testEcho() throws Exception {

        //GIVEN
        PostRequest post  = new PostRequest();
        post.setTitle("Title");
        post.setText("bla bla");
        String body = gson.toJson(post);

        //WHEN
        ResultActions resultActions = mvc.perform(MockMvcRequestBuilders.post(BlogConstants.BLOG_POSTS_URL+"/post/echo")
                .content(body)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                //THEN
                .andExpect(status().isOk());
        Post postResult = gson.fromJson(resultActions.andReturn().getResponse().getContentAsString(),Post.class);
        assertNotNull(postResult);
        assertEquals("bla bla",postResult.getText());
        assertEquals("Title",postResult.getTitle());

    }

    @Test
    public void testAddPost() throws Exception {
        //GIVEN
        PostRequest post  = new PostRequest();
        post.setTitle("TestAddPost");
        post.setText("bla bla");
        String body = gson.toJson(post);

        //WHEN
        ResultActions resultActions = mvc.perform(MockMvcRequestBuilders.post(BlogConstants.BLOG_POSTS_URL+"/post")
                .header("username","addUser")
                .content(body)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        PostId postId = gson.fromJson(resultActions.andReturn().getResponse().getContentAsString(),PostId.class);

        //THEN
        ResultActions resultActions2 = mvc.perform(MockMvcRequestBuilders.get(BlogConstants.BLOG_POSTS_URL+"/post/"+postId.getId())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        String getResponce = resultActions2.andReturn().getResponse().getContentAsString();
        Post postResult = gson.fromJson(getResponce,Post.class);
        assertEquals("addUser",postResult.getCreator());
        assertEquals("bla bla",postResult.getText());
        assertEquals("TestAddPost",postResult.getTitle());
        assertNotNull(postResult.getCreated());
        assertEquals(0,postResult.getRating().intValue());
    }

    @Test
    public void testAddPost_Negative() throws Exception {
        //GIVEN
        PostRequest post  = new PostRequest();
        post.setTitle("TestAddPost");
        String body = gson.toJson(post);

        //WHEN
        ResultActions resultActions = mvc.perform(MockMvcRequestBuilders.post(BlogConstants.BLOG_POSTS_URL+"/post")
                .header("username","addUser")
                .content(body)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
        BlogErrorResponse error = gson.fromJson(resultActions.andReturn().getResponse().getContentAsString(),BlogErrorResponse.class);

        //THEN
        assertNotNull(error);
        assertEquals("Bad Request",error.error);
    }



    @Test
    public void testUpdatePost() throws Exception {
        //GIVEN
        PostRequest post  = new PostRequest();
        post.setTitle("TestEditPost");
        post.setText("bla bla");
        String body = gson.toJson(post);

        ResultActions resultActions = mvc.perform(MockMvcRequestBuilders.post(BlogConstants.BLOG_POSTS_URL+"/post")
                .header("username","addUser")
                .content(body)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        PostId postId = gson.fromJson(resultActions.andReturn().getResponse().getContentAsString(),PostId.class);
        post.setTitle("TestEditPostUpdated");
        post.setText("updatedText");

        //WHEN
        mvc.perform(MockMvcRequestBuilders.put(BlogConstants.BLOG_POSTS_URL+"/post/"+postId.getId())
                .header("username","addUser")
                .content(gson.toJson(post))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        //THEN
        ResultActions resultActions2 = mvc.perform(MockMvcRequestBuilders.get(BlogConstants.BLOG_POSTS_URL+"/post/"+postId.getId())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        String getResponce = resultActions2.andReturn().getResponse().getContentAsString();
        Post postUpdated = gson.fromJson(getResponce,Post.class);
        assertEquals("addUser",postUpdated.getCreator());
        assertEquals("updatedText",postUpdated.getText());
        assertEquals("TestEditPost",postUpdated.getTitle());
        assertNotNull(postUpdated.getCreated());
        assertNotNull(postUpdated.getLastModified());
        assertEquals(0,postUpdated.getRating().intValue());


    }

    @Test
    public void testUpdatePost_Negative() throws Exception {
        //GIVEN
        PostRequest post  = new PostRequest();
        post.setTitle("TestEditPost");
        post.setText("bla bla");
        String body = gson.toJson(post);

        ResultActions resultActions = mvc.perform(MockMvcRequestBuilders.post(BlogConstants.BLOG_POSTS_URL+"/post")
                .header("username","addUser")
                .content(body)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        PostId postId = gson.fromJson(resultActions.andReturn().getResponse().getContentAsString(),PostId.class);
        post.setTitle("TestEditPostUpdated");
        post.setText(null);

        //WHEN
        ResultActions resultActions2 = mvc.perform(MockMvcRequestBuilders.put(BlogConstants.BLOG_POSTS_URL+"/post/"+postId.getId())
                .header("username","addUser")
                .content(gson.toJson(post))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
        BlogErrorResponse error = gson.fromJson(resultActions2.andReturn().getResponse().getContentAsString(),BlogErrorResponse.class);

        //THEN
        assertNotNull(error);
        assertEquals("Missing mandatory parameter: text",error.errorMessage);
        assertEquals("Bad Request",error.error);
    }

    @Test
    public void testDeletePost() throws Exception {
        //GIVEN
        PostRequest post  = new PostRequest();
        post.setTitle("TestDeletePost");
        post.setText("bla bla");
        String body = gson.toJson(post);

        ResultActions resultActions = mvc.perform(MockMvcRequestBuilders.post(BlogConstants.BLOG_POSTS_URL+"/post")
                .header("username","addUser")
                .content(body)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        String postId = gson.fromJson(resultActions.andReturn().getResponse().getContentAsString(),PostId.class).getId();

        //WHEN
        mvc.perform(MockMvcRequestBuilders.delete("/blog/posts/post/"+postId)
                .header("username","addUser")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        //THEN
        ResultActions resultActions2 = mvc.perform(MockMvcRequestBuilders.get(BlogConstants.BLOG_POSTS_URL+"/post/"+postId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        String getResponce = resultActions2.andReturn().getResponse().getContentAsString();
        assertEquals(getResponce,"");
        PostId postUpdated = gson.fromJson(getResponce,PostId.class);
        assertNull(postUpdated);

    }


    @Test
    public void testUpVote() throws Exception {
        //GIVEN
        PostRequest post  = new PostRequest();
        post.setTitle("TestUpvotePost");
        post.setText("bla bla");
        String body = gson.toJson(post);

        ResultActions resultActions = mvc.perform(MockMvcRequestBuilders.post(BlogConstants.BLOG_POSTS_URL+"/post")
                .header("username","voteUser")
                .content(body)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        String postId = gson.fromJson(resultActions.andReturn().getResponse().getContentAsString(),PostId.class).getId();

        //WHEN
        mvc.perform(MockMvcRequestBuilders.post(BlogConstants.BLOG_POSTS_URL+"/post/"+postId+"/upvote")
                .header("username","voteUser")
                .content(body)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        //THEN
        ResultActions resultActions2 = mvc.perform(MockMvcRequestBuilders.get(BlogConstants.BLOG_POSTS_URL+"/post/"+postId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        String getResponce = resultActions2.andReturn().getResponse().getContentAsString();
        Post postVoted = gson.fromJson(getResponce,Post.class);
        assertNotNull(postVoted);
        assertEquals(1,postVoted.getUpvoters().size());
        assertEquals(0,postVoted.getDownvoters().size());
        assertEquals("voteUser",postVoted.getUpvoters().get(0));
        assertEquals(1,postVoted.getRating().intValue());

    }
    @Test
    public void testUpVote_Negative() throws Exception {
        //GIVEN
        PostRequest post  = new PostRequest();
        post.setTitle("TestUpvotePost");
        post.setText("bla bla");
        String body = gson.toJson(post);

        ResultActions resultActions = mvc.perform(MockMvcRequestBuilders.post(BlogConstants.BLOG_POSTS_URL+"/post")
                .header("username","voteUser")
                .content(body)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        String postId = gson.fromJson(resultActions.andReturn().getResponse().getContentAsString(),PostId.class).getId();

        //WHEN
        mvc.perform(MockMvcRequestBuilders.post(BlogConstants.BLOG_POSTS_URL+"/post/"+postId+"/upvote")
                .header("username","voteUser")
                .content(body)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        //WHEN
        ResultActions resultActions2 = mvc.perform(MockMvcRequestBuilders.post(BlogConstants.BLOG_POSTS_URL+"/post/"+postId+"/upvote")
                .header("username","voteUser")
                .content(body)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
        //THEN

        String getResponce = resultActions2.andReturn().getResponse().getContentAsString();
        BlogErrorResponse error = gson.fromJson(getResponce,BlogErrorResponse.class);
        assertNotNull(error);
        assertEquals("Bad Request",error.error);
    }

    @Test
    public void testDownVote() throws Exception {
        //GIVEN
        PostRequest post  = new PostRequest();
        post.setTitle("TestDownvotePost");
        post.setText("bla bla");
        String body = gson.toJson(post);

        ResultActions resultActions = mvc.perform(MockMvcRequestBuilders.post(BlogConstants.BLOG_POSTS_URL+"/post")
                .header("username","voteUser")
                .content(body)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        String postId = gson.fromJson(resultActions.andReturn().getResponse().getContentAsString(),Post.class).getId();

        //WHEN
        mvc.perform(MockMvcRequestBuilders.post(BlogConstants.BLOG_POSTS_URL+"/post/"+postId+"/downvote")
                .header("username","voteUser")
                .content(body)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        //THEN
        ResultActions resultActions2 = mvc.perform(MockMvcRequestBuilders.get(BlogConstants.BLOG_POSTS_URL+"/post/"+postId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        String getResponce = resultActions2.andReturn().getResponse().getContentAsString();
        Post postVoted = gson.fromJson(getResponce,Post.class);
        assertNotNull(postVoted);
        assertEquals(0,postVoted.getUpvoters().size());
        assertEquals(1,postVoted.getDownvoters().size());
        assertEquals("voteUser",postVoted.getDownvoters().get(0));
        assertEquals(-1,postVoted.getRating().intValue());

    }



    @Test
    public void testGetTopPosts() throws Exception {
        //GIVEN
        testDeletePosts();
        testDownVote();
        testDownVote();
        testUpVote();
        testUpVote();
        testUpVote();
        testAddPost();

        //WHEN
        ResultActions resultActions = mvc.perform(MockMvcRequestBuilders.get(BlogConstants.BLOG_POSTS_URL+"/top?count=5")
                .header("username","topUser")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        //THEN
        List<Post> posts = Arrays.asList(gson.fromJson(resultActions.andReturn().getResponse().getContentAsString(),Post[].class));
        assertNotNull(posts);
        assertEquals(5,posts.size());
        assertEquals(1,posts.get(0).getRating().intValue());
        assertEquals(1,posts.get(1).getRating().intValue());
        assertEquals(1,posts.get(2).getRating().intValue());
        assertEquals(0,posts.get(3).getRating().intValue());
        assertEquals(-1,posts.get(4).getRating().intValue());
    }

    @Test
    public void testGetPosts() throws Exception {
        //WHEN
        ResultActions resultActions = mvc.perform(MockMvcRequestBuilders.get(BlogConstants.BLOG_POSTS_URL)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        String response = resultActions.andReturn().getResponse().getContentAsString();
        //THEN
        assertNotNull(response);
    }

    @Test
    public void testDeletePosts() throws Exception {
        //WHEN
        mvc.perform(MockMvcRequestBuilders.delete(BlogConstants.BLOG_POSTS_URL)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        //THEN
        ResultActions resultActions = mvc.perform(MockMvcRequestBuilders.get(BlogConstants.BLOG_POSTS_URL)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        String response = resultActions.andReturn().getResponse().getContentAsString();
        assertEquals("[]",response);
    }

}