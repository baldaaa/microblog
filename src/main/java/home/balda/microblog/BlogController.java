/**
 * Created by katia on 30/04/2018.
 */

package home.balda.microblog;


import home.balda.microblog.entity.Post;
import home.balda.microblog.entity.PostId;
import home.balda.microblog.representation.PostRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static home.balda.microblog.BlogConstants.BLOG_POSTS_URL;


@Api(value="microBlog", description="Simple blog implementation")
@RestController
@RequestMapping(path = BLOG_POSTS_URL)
public class BlogController {

    @Autowired
    private BlogService blogService;

    @ApiOperation(value = "Get all posts")
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<Post>> getPosts() {
        return new ResponseEntity<List<Post>>(this.blogService.getPosts(), HttpStatus.OK);
    }

    @ApiOperation(value = "Get existing post by post Id")
    @RequestMapping(method = RequestMethod.GET, path = "/post/{id}")
    public ResponseEntity<Post> getPost(@ApiParam(value = "Post unique Id") @PathVariable(name = "id") String id) {
        return new ResponseEntity<Post>(this.blogService.getPost(id), HttpStatus.OK);
    }

    @ApiOperation(value = "Add new post")
    @RequestMapping(method = RequestMethod.POST, path = "/post")
    public ResponseEntity<PostId> addPost(@ApiParam(value = "Blogger unique name") @RequestHeader(value = "username") String username,
                                          @RequestBody PostRequest post) {
        return new ResponseEntity<PostId>(new PostId(this.blogService.addPost(post,username)), HttpStatus.OK);
    }

    @ApiOperation(value = "Update post data by post Id")
    @RequestMapping(method = RequestMethod.PUT, path = "/post/{id}")
    public ResponseEntity<PostId> updatePost(@ApiParam(value = "Post unique Id") @PathVariable(name = "id") String id,
                                             @RequestBody PostRequest post) {
        return new ResponseEntity<PostId>(new PostId(this.blogService.updatePost(id, post)), HttpStatus.OK);
    }

    @ApiOperation(value = "Delete post by post Id")
    @RequestMapping(method = RequestMethod.DELETE, path = "/post/{id}")
    public ResponseEntity<PostId> deletePost(@ApiParam(value = "Post unique Id") @PathVariable(name = "id") String id) {
        return new ResponseEntity<PostId>(new PostId(this.blogService.deletePost(id)), HttpStatus.OK);
    }

    @ApiOperation(value = "Delete all posts")
    @RequestMapping(method = RequestMethod.DELETE)
    public void deletePosts() {
        this.blogService.deletePosts();
    }

    @ApiOperation(value = "Up vote for post")
    @RequestMapping(method = RequestMethod.POST, path = "/post/{id}/upvote")
    public void upVote(@ApiParam(value = "Blogger unique name") @RequestHeader(value = "username") String username,
                       @ApiParam(value = "Post unique Id") @PathVariable(name = "id") String id) {
        this.blogService.upvote(id, username);
    }

    @ApiOperation(value = "Down vote for post")
    @RequestMapping(method = RequestMethod.POST, path = "/post/{id}/downvote")
    public void downVote(@ApiParam(value = "Blogger unique name") @RequestHeader(value = "username") String username,
                         @ApiParam(value = "Post unique Id") @PathVariable(name = "id") String id) {
        this.blogService.downvote(id, username);
    }

    @ApiOperation(value = "Echo test",hidden = true)
    @RequestMapping(method = RequestMethod.POST, path = "/post/echo")
    public ResponseEntity<Post> echo(@RequestBody Post post) {
        return new ResponseEntity<Post>(this.blogService.echo(post), HttpStatus.OK);
    }

    @ApiOperation(value = "Get top posts, ordered by rating and creation date")
    @RequestMapping(method = RequestMethod.GET, path = "/top")
    public ResponseEntity<List<Post>> getTopPosts(@ApiParam(value = "Requested top posts count", defaultValue = "10") @RequestParam(name = "count") Optional<Integer> count) {
        return new ResponseEntity<List<Post>>(this.blogService.getTopPosts(count), HttpStatus.OK);
    }



}
