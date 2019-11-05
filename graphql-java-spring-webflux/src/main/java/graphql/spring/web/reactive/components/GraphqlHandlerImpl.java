package graphql.spring.web.reactive.components;

import graphql.ExecutionResult;
import graphql.spring.web.reactive.*;
import graphql.spring.web.reactive.configuration.BeanNames;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Component(BeanNames.GRAPHQL_HANDLER)
public class GraphqlHandlerImpl implements GraphqlHandler {

    private final GraphQLInvocation graphQLInvocation;

    private final ExecutionResultHandler executionResultHandler;

    private final JsonSerializer jsonSerializer;

    public GraphqlHandlerImpl(GraphQLInvocation graphQLInvocation, ExecutionResultHandler executionResultHandler, JsonSerializer jsonSerializer) {
        this.graphQLInvocation = graphQLInvocation;
        this.executionResultHandler = executionResultHandler;
        this.jsonSerializer = jsonSerializer;
    }

    @Override
    public Mono<ServerResponse> invokeByParams(ServerRequest request) {
        String query = request.queryParam("query").orElse(null);
        String operationName = request.queryParam("operationName").orElse(null);
        Map<String, Object> variables = request.queryParam("variables")
                .map(this::convertVariablesJson)
                .orElseGet(HashMap::new);
        Object result = executeRequest(query, operationName, variables, request.exchange());
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(result, Object.class);
    }

    @Override
    public Mono<ServerResponse> invokeByParamsAndBody(ServerRequest request) {
        String operationName = request.queryParam("operationName").orElse(null);
        Map<String, Object> variables = request.queryParam("variables")
                .map(this::convertVariablesJson)
                .orElseGet(HashMap::new);
        Mono<ExecutionResult> result = request.bodyToMono(String.class)
                .map(it -> new GraphQLInvocationData(it, operationName, variables))
                .flatMap(it -> graphQLInvocation.invoke(it, request.exchange()));
        Object finalResult = executionResultHandler.handleExecutionResult(result, request.exchange().getResponse());
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(finalResult, Object.class);
    }

    @Override
    public Mono<ServerResponse> invokeByBody(ServerRequest request) {
        Mono<ExecutionResult> result = request.bodyToMono(String.class)
                .map(it -> jsonSerializer.deserialize(it, GraphQLRequestBody.class))
                .map(it -> new GraphQLInvocationData(it.getQuery(), it.getOperationName(), it.getVariables()))
                .flatMap(it -> graphQLInvocation.invoke(it, request.exchange()));
        Object finalResult = executionResultHandler.handleExecutionResult(result, request.exchange().getResponse());
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(finalResult, Object.class);
    }

    private Map<String, Object> convertVariablesJson(String jsonMap) {
        if (jsonMap == null) {
            return Collections.emptyMap();
        }
        return jsonSerializer.deserialize(jsonMap, Map.class);
    }

    private Object executeRequest(String query, String operationName, Map<String, Object> variables, ServerWebExchange serverWebExchange) {
        GraphQLInvocationData invocationData = new GraphQLInvocationData(query, operationName, variables);
        Mono<ExecutionResult> executionResult = graphQLInvocation.invoke(invocationData, serverWebExchange);
        return executionResultHandler.handleExecutionResult(executionResult, serverWebExchange.getResponse());
    }
}