package testconfig;

import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.GraphQL;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.reactive.config.EnableWebFlux;

@Configuration
@EnableWebFlux
@ComponentScan(basePackages = "graphql.spring.web.reactive")
public class TestAppConfig {


    @Bean
    public GraphQL graphQL() {
        GraphQL graphql = Mockito.mock(GraphQL.class);
        return graphql;
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public WebRequest webRequest() {
        return Mockito.mock(WebRequest.class);
    }

}
