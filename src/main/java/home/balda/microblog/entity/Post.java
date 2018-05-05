package home.balda.microblog.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.data.elasticsearch.annotations.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static home.balda.microblog.BlogConstants.DATE_FORMAT;
import static home.balda.microblog.BlogConstants.POST;
import static home.balda.microblog.BlogConstants.POSTS;

/**
 * Created by katia on 30/04/2018.
 */

@Document(indexName= POSTS, type = POST, shards = 1, replicas = 0, refreshInterval = "-1")
@ApiModel(value = "Post Response")
public class Post extends PostId {

    public static final String ID = "id";
    public static final String CREATOR = "creator";
    public static final String TITLE = "title";
    public static final String TEXT = "text";
    public static final String UPVOTERS = "upvoters";
    public static final String DOWNVOTERS = "downvoters";
    public static final String RATING = "rating";
    public static final String CREATED = "created";
    public static final String LAST_MODIFIED = "lastModified";

    public Post(String id) {
        super(id);
    }
    public Post(String title, String text) {
        this.title = title;
        this.text = text;
    }
    public Post() {
    }

    @ApiModelProperty(notes = "Post creator")
    @Field(type = FieldType.text)
    private String creator;

    @ApiModelProperty(notes = "Post title")
    @Field(type = FieldType.text)
    private String title;

    @ApiModelProperty(notes = "Post text")
    @Field(type = FieldType.text)
    private String text;

    @ApiModelProperty(notes = "List of up voted users")
    private List<String> upvoters;

    @ApiModelProperty(notes = "List of down voted")
    private List<String> downvoters;

    @ApiModelProperty(notes = "Post rating")
    @Field(type = FieldType.Integer)
    private Integer rating;

    @ApiModelProperty(notes = "Post creation date")
    @Field(type= FieldType.Date,format = DateFormat.basic_date_time)
    @JsonFormat (shape = JsonFormat.Shape.STRING, pattern =DATE_FORMAT)
    private Date created;

    @ApiModelProperty(notes = "Post last modification date")
    @Field(type= FieldType.Date,format = DateFormat.basic_date_time)
    @JsonFormat (shape = JsonFormat.Shape.STRING, pattern =DATE_FORMAT)
    private Date lastModified;


    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public Integer getRating() {
        return Optional.ofNullable(rating).orElse(0);
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<String> getUpvoters() {
        return Optional.ofNullable(upvoters).orElse(new ArrayList<>());
    }

    public void setUpvoters(List<String> upvoters) {
        this.upvoters = upvoters;
    }

    public void setDownvoters(List<String> downvoters) {
        this.downvoters = downvoters;
    }

    public List<String> getDownvoters() {
        return Optional.ofNullable(downvoters).orElse(new ArrayList<>());
    }


    public void addUpVoter(String username){
        getUpvoters().add(username);
        setRating(getUpvoters().size() - getDownvoters().size());
    }

    public void addDownVoter(String username){
        getDownvoters().add(username);
        setRating(getUpvoters().size() - getDownvoters().size());
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

}
