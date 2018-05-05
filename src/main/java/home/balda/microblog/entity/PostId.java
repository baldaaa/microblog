package home.balda.microblog.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * Created by katia on 04/05/2018.
 */
@ApiModel(value = "Id Response")
public class PostId {

    public PostId(String id) {
        this.id = id;
    }
    public PostId() {

    }
    @ApiModelProperty(notes = "Post unique Id")
    @Id
    @Field(type = FieldType.keyword)
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
