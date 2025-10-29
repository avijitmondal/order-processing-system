package com.avijitmondal.ops.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SessionIdFilterTest {

    private SessionIdFilter filter;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @BeforeEach
    void setUp() {
        filter = new SessionIdFilter();
        MDC.clear();
    }

    @Test
    void doFilter_withSessionIdCookie_addsCookieToMDC() throws IOException, ServletException {
        Cookie sessionCookie = new Cookie("session_id", "test-session-123");
        when(request.getCookies()).thenReturn(new Cookie[]{sessionCookie});

        filter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNull(MDC.get("sessionId")); // MDC should be cleared after filter
    }

    @Test
    void doFilter_withoutSessionIdCookie_setsEmptyMDC() throws IOException, ServletException {
        Cookie otherCookie = new Cookie("other_cookie", "value");
        when(request.getCookies()).thenReturn(new Cookie[]{otherCookie});

        filter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNull(MDC.get("sessionId"));
    }

    @Test
    void doFilter_withNoCookies_setsEmptyMDC() throws IOException, ServletException {
        when(request.getCookies()).thenReturn(null);

        filter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNull(MDC.get("sessionId"));
    }

    @Test
    void doFilter_withEmptySessionId_setsEmptyMDC() throws IOException, ServletException {
        Cookie sessionCookie = new Cookie("session_id", "");
        when(request.getCookies()).thenReturn(new Cookie[]{sessionCookie});

        filter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNull(MDC.get("sessionId"));
    }

    @Test
    void doFilter_cleansMDC_evenOnException() throws IOException, ServletException {
        Cookie sessionCookie = new Cookie("session_id", "test-session");
        when(request.getCookies()).thenReturn(new Cookie[]{sessionCookie});
        doThrow(new ServletException("Test exception")).when(filterChain).doFilter(request, response);

        assertThrows(ServletException.class, () -> filter.doFilter(request, response, filterChain));
        
        assertNull(MDC.get("sessionId")); // MDC should still be cleared
    }
}
