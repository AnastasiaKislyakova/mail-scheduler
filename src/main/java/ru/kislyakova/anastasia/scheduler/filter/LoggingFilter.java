package ru.kislyakova.anastasia.scheduler.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class LoggingFilter implements WebFilter {

    private static final Logger logger = LoggerFactory.getLogger(LoggingFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        String path = request.getPath().pathWithinApplication().value();
        HttpMethod method = request.getMethod();
        MultiValueMap<String, String> queryParams = request.getQueryParams();
        logger.info("{}: {} {}", method, path, queryParams);

        return chain.filter(exchange)
                .doOnSuccess((it) -> {
                    HttpStatus statusCode = response.getStatusCode();
                    String logMessage = String.format("%s: %s %s %s", method, path, queryParams, statusCode);
                    if (statusCode != null && statusCode.is2xxSuccessful()) {
                        logger.info(logMessage);
                    } else if (statusCode != null && statusCode.is4xxClientError()){
                        logger.warn(logMessage);
                    } else {
                        logger.error(logMessage);
                    }
                })
                .doOnError((ex) -> {
                    logger.error("{}: {} {} {} finished with error", method, path, queryParams,
                            response.getStatusCode(), ex);
                });
    }
}
