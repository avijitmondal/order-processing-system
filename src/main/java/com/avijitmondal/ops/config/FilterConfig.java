package com.avijitmondal.ops.config;

import com.avijitmondal.ops.filter.SessionIdFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

/**
 * Configuration for custom servlet filters.
 * Registers the SessionIdFilter for tracking user sessions.
 */
@Configuration
public class FilterConfig {

    /**
     * Registers the SessionIdFilter to track session IDs for all requests.
     * This filter runs with the highest precedence to ensure session ID is set early in the request processing chain.
     */
    @Bean
    public FilterRegistrationBean<SessionIdFilter> sessionIdFilter() {
        var registrationBean = new FilterRegistrationBean<SessionIdFilter>();
        registrationBean.setFilter(new SessionIdFilter());
        registrationBean.addUrlPatterns("/*");
        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return registrationBean;
    }
}
