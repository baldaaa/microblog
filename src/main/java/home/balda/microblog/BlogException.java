package home.balda.microblog;

import com.sun.javafx.binding.StringFormatter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by katia on 02/05/2018.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BlogException extends RuntimeException {


    static final String ALREADY_VOTED_MESSAGE = "User %s is already voted";
    static final String MISSING_MANDATORY_PARAMETER = "Missing mandatory parameter: %s";

    public BlogException(String message,Object messageParameter) {
        super(String.format(message,messageParameter));
    }
}
