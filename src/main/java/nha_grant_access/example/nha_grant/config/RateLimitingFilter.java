package nha_grant_access.example.nha_grant.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;


import java.io.IOException;
import java.time.Instant;
import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.TimeUnit;

@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    private static final int MAX_REQUESTS = 10;
    private static final long TIME_WINDOW_MS = 1000;

    // IP -> List of request timestamps
    private final Cache<String, Deque<Long>> requestCache = Caffeine.newBuilder()
            .expireAfterAccess(10, TimeUnit.SECONDS) // Clean inactive IPs after 10 seconds
            .build();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String ip = request.getRemoteAddr();
        long now = Instant.now().toEpochMilli();

        Deque<Long> timestamps = requestCache.get(ip, key -> new ConcurrentLinkedDeque<>());

        synchronized (timestamps) {
            // Remove timestamps older than TIME_WINDOW_MS
            while (!timestamps.isEmpty() && (now - timestamps.peekFirst()) > TIME_WINDOW_MS) {
                timestamps.pollFirst();
            }

            if (timestamps.size() >= MAX_REQUESTS) {
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.getWriter().write("Too many requests - Rate limit exceeded.");
                return;
            }

            timestamps.addLast(now);
        }

        filterChain.doFilter(request, response);
    }
}
