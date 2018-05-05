package home.balda.microblog.representation;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Created by katia on 05/05/2018.
 */
@ApiModel(value = "Post Request")
public class PostRequest {

    @ApiModelProperty(notes = "Post title")
    private String title;

    @ApiModelProperty(notes = "Post text")
    private String text;

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
}
