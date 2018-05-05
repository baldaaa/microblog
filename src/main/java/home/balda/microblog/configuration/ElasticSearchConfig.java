package home.balda.microblog.configuration;

import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;


import java.net.InetAddress;

/**
 * Created by katia on 30/04/2018.
 */

@Configuration
@EnableElasticsearchRepositories(basePackages = "home.balda.microblog.repository.elastic")
@PropertySource("classpath:application.properties")
public class ElasticSearchConfig {

    @Autowired
    Environment env;

    @Bean
    public Client client() throws Exception {
        Settings settings = Settings.builder().put("cluster.name",env.getProperty("elasticsearch.clustername")).build();
        return new PreBuiltTransportClient(settings)
                .addTransportAddress(
                        new InetSocketTransportAddress(InetAddress.getByName(env.getProperty("elasticsearch.host")),
                                Integer.parseInt(env.getProperty("elasticsearch.port"))));
    }

    @Bean
    public ElasticsearchOperations elasticsearchTemplate() throws Exception {
        return new ElasticsearchTemplate(client());
    }

}
