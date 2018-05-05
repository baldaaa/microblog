package home.balda.microblog.representation;

/**
 * Created by katia on 05/05/2018.
 */
public class BlogErrorResponse{

    public BlogErrorResponse(String timestamp, String error, String errorMessage) {
        this.timestamp = timestamp;
        this.error = error;
        this.errorMessage = errorMessage;
    }

    public String timestamp;
    public String error;
    public String errorMessage;

}
