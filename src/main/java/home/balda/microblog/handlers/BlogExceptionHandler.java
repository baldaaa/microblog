package home.balda.microblog.handlers;

import com.google.gson.Gson;
import home.balda.microblog.BlogException;
import home.balda.microblog.representation.BlogErrorResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Created by katia on 05/05/2018.
 */
@ControllerAdvice
public class BlogExceptionHandler extends ResponseEntityExceptionHandler {



    @ExceptionHandler(value = { BlogException.class})
    protected ResponseEntity<Object> handleBlogError(RuntimeException ex, WebRequest request) {
        String bodyOfResponse = new Gson().toJson(
                new BlogErrorResponse(System.currentTimeMillis()+"",
                        HttpStatus.BAD_REQUEST.getReasonPhrase(),
                        ex.getMessage()),BlogErrorResponse.class);
        return handleExceptionInternal(ex, bodyOfResponse,
                new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(value = { RuntimeException.class})
    protected ResponseEntity<Object> handleServerError(RuntimeException ex, WebRequest request) {

        String bodyOfResponse = new Gson().toJson(
                new BlogErrorResponse(System.currentTimeMillis()+"",
                        HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                        ex.getMessage()),BlogErrorResponse.class);
        return handleExceptionInternal(ex, bodyOfResponse,
                new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }


}
