package graphql.spring.web.reactive.configuration;

import graphql.spring.web.reactive.GraphqlHandler;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import java.util.Objects;

import static org.springframework.web.reactive.function.server.RequestPredicates.contentType;
import static org.springframework.web.reactive.function.server.RequestPredicates.queryParam;

@Configuration
public class GraphqlConfiguration {

    private final String endpoint;

    public GraphqlConfiguration(@Value("${graphql.url:/graphql}") String endpoint) {
        this.endpoint = endpoint;
    }

    @Bean(BeanNames.GRAPHQL_ROUTER_FUNCTION)
    public RouterFunction<ServerResponse> routerFunction(@Qualifier(BeanNames.GRAPHQL_HANDLER) GraphqlHandler handler) {
        return RouterFunctions.route()
                .GET(endpoint, handler::invokeByParams)
                .POST(endpoint, queryParam("query", Objects::nonNull), handler::invokeByParams)
                .POST(endpoint, contentType(MediaType.APPLICATION_JSON), handler::invokeByBody)
                .POST(endpoint, contentType(new MediaType("application", "graphql")), handler::invokeByParamsAndBody)
                .build();
    }
}