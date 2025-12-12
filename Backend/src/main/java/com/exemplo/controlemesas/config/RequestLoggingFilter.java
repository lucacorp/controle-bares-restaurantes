package com.exemplo.controlemesas.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestLoggingFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(RequestLoggingFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if (logger.isDebugEnabled()) {
            logger.debug("Incoming request: {} {}", request.getMethod(), request.getRequestURI());
            String origin = request.getHeader("Origin");
            String auth = request.getHeader("Authorization");
            logger.debug("Origin: {} | Authorization: {}", origin, auth == null ? "<none>" : "[present]");

            Enumeration<String> names = request.getHeaderNames();
            if (names != null) {
                StringBuilder sb = new StringBuilder("Headers:\n");
                Collections.list(names).forEach(name -> sb.append(name).append(": ").append(request.getHeader(name)).append("\n"));
                logger.debug(sb.toString());
            }
        }

        // Additional, very detailed logging for temporary debugging: will only run when TRACE is enabled
        if (logger.isTraceEnabled()) {
            StringBuilder sb = new StringBuilder("Detailed incoming request:\n");
            sb.append("Method: ").append(request.getMethod()).append("\n");
            sb.append("RequestURI: ").append(request.getRequestURI()).append("\n");

            Enumeration<String> headerNames = request.getHeaderNames();
            if (headerNames != null) {
                sb.append("Headers:\n");
                Collections.list(headerNames).forEach(name -> sb.append(name).append(": ").append(request.getHeader(name)).append("\n"));
            }

            Enumeration<String> paramNames = request.getParameterNames();
            if (paramNames != null) {
                sb.append("Parameters:\n");
                Collections.list(paramNames).forEach(name -> sb.append(name).append(": ").append(request.getParameter(name)).append("\n"));
            }

            logger.trace(sb.toString());
        }

        filterChain.doFilter(request, response);
    }
}