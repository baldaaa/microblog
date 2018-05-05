package home.balda.microblog;
/**
 * Created by katia on 30/04/2018.
 */
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.http.converter.json.GsonHttpMessageConverter;

import static home.balda.microblog.BlogConstants.DATE_FORMAT;

@SpringBootApplication
@EnableSpringDataWebSupport
public class BlogApplication {


    public static void main(String[] args) {
        SpringApplication.run(BlogApplication.class, args);
    }

    @Bean
    public HttpMessageConverters additionalConverters() {
        Gson gson = new GsonBuilder().setDateFormat(DATE_FORMAT).create();
        GsonHttpMessageConverter converter = new GsonHttpMessageConverter();
        converter.setGson(gson);
        return new HttpMessageConverters(converter);
    }


}
