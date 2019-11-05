package graphql.spring.web.reactive;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
@ConditionalOnWebApplication
@ComponentScan(basePackages = "graphql.spring.web.reactive")
public class GraphQLEndpointConfiguration {

    @Autowired
    ApplicationContext applicationContext;

    @PostConstruct
    public void init() {
    }
}
