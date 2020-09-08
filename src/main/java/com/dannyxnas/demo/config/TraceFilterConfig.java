package com.dannyxnas.demo.config;

import org.springframework.boot.actuate.autoconfigure.trace.http.HttpTraceProperties;
import org.springframework.boot.actuate.trace.http.HttpExchangeTracer;
import org.springframework.boot.actuate.trace.http.HttpTraceRepository;
import org.springframework.boot.actuate.trace.http.InMemoryHttpTraceRepository;
import org.springframework.boot.actuate.web.trace.reactive.HttpTraceWebFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnWebApplication
@ConditionalOnProperty(prefix = "management.trace.http", name = "enabled", matchIfMissing = true)
@EnableConfigurationProperties(HttpTraceProperties.class)
public class TraceFilterConfig {

    //Objects storing HttpTrace information
    @Bean
    @ConditionalOnMissingBean(HttpTraceRepository.class)
    public InMemoryHttpTraceRepository traceRepository() {
        return new InMemoryHttpTraceRepository();
    }
    //Create HttpTrace object Exchange
    @Bean
    @ConditionalOnMissingBean
    public HttpExchangeTracer httpExchangeTracer(HttpTraceProperties traceProperties) {
        return new HttpExchangeTracer(traceProperties.getInclude());
    }

    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
    static class ServletTraceFilterConfiguration {
        //Register our customized Filter in Bean mode to take effect
        @Bean
        @ConditionalOnMissingBean
        public CustomerHttpTraceFilter httpTraceFilter(HttpTraceRepository repository,
                                                       HttpExchangeTracer tracer) {
            return new CustomerHttpTraceFilter(repository,tracer);
        }
    }

    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
    static class ReactiveTraceFilterConfiguration {

        @Bean
        @ConditionalOnMissingBean
        public HttpTraceWebFilter httpTraceWebFilter(HttpTraceRepository repository,
                                                     HttpExchangeTracer tracer, HttpTraceProperties traceProperties) {
            return new HttpTraceWebFilter(repository, tracer,
                    traceProperties.getInclude());
        }
    }
}
