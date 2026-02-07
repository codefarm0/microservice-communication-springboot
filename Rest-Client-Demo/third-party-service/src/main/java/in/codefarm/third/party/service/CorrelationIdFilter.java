package in.codefarm.third.party.service;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Component
public class CorrelationIdFilter implements Filter {

    public static final String CORRELATION_ID = "X-Correlation-Id";

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain) throws IOException, ServletException {

        HttpServletRequest http = (HttpServletRequest) request;
        String correlationId =
                Optional.ofNullable(http.getHeader(CORRELATION_ID))
                        .orElse(UUID.randomUUID().toString());

        MDC.put(CORRELATION_ID, correlationId);

        try {
            chain.doFilter(request, response);
        } finally {
            MDC.clear();
        }
    }
}
