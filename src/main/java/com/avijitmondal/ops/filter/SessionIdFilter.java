package com.avijitmondal.ops.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.MDC;

import java.io.IOException;
import java.util.Arrays;

/**
 * Servlet filter that extracts session ID from cookies and adds it to the logging MDC.
 * This allows the session ID to be included in all log statements during request processing.
 */
public class SessionIdFilter implements Filter {

    private static final String SESSION_ID_KEY = "sessionId";
    private static final String SESSION_ID_COOKIE = "session_id";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        var httpRequest = (HttpServletRequest) request;
        var sessionId = extractSessionId(httpRequest);
        
        try {
            // Put session ID in MDC for logging
            MDC.put(SESSION_ID_KEY, sessionId != null && !sessionId.isEmpty() ? sessionId : "");
            
            chain.doFilter(request, response);
        } finally {
            // Clean up MDC
            MDC.remove(SESSION_ID_KEY);
        }
    }

    /**
     * Extracts session ID from the request cookies.
     * @param request HTTP request containing cookies
     * @return session ID if found, empty string otherwise
     */
    private String extractSessionId(HttpServletRequest request) {
        var cookies = request.getCookies();
        if (cookies != null) {
            return Arrays.stream(cookies)
                    .filter(cookie -> SESSION_ID_COOKIE.equals(cookie.getName()))
                    .findFirst()
                    .map(Cookie::getValue)
                    .orElse("");
        }
        return "";
    }
}
