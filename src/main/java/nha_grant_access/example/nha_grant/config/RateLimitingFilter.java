package nha_grant_access.example.nha_grant.config;

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
import java.util.concurrent.*;

@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    private static final int MAX_REQUESTS = 5;
    private static final long TIME_WINDOW_MS = 1000;

    // Holds request timestamps for each IP
    private final ConcurrentHashMap<String, Deque<Long>> requestLogMap = new ConcurrentHashMap<>();

    // Holds locks for each IP to synchronize access
    private final ConcurrentHashMap<String, Object> ipLocks = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String ip = request.getRemoteAddr();
        long now = Instant.now().toEpochMilli();

        // Ensure a lock object per IP (single instance)
        Object ipLock = ipLocks.computeIfAbsent(ip, k -> new Object());

        boolean allowed;
        synchronized (ipLock) {
            // Get or create timestamp queue
            Deque<Long> timestampsQueue = requestLogMap.computeIfAbsent(ip, k -> new ConcurrentLinkedDeque<>());

            // Remove timestamps older than the window
            while (!timestampsQueue.isEmpty() && now - timestampsQueue.peekFirst() > TIME_WINDOW_MS) {
                timestampsQueue.pollFirst();
            }

            if (timestampsQueue.size() < MAX_REQUESTS) {
                timestampsQueue.addLast(now);
                allowed = true;
            } else {
                allowed = false;
            }
        }

        if (allowed) {
            filterChain.doFilter(request, response);
        } else {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.getWriter().write("Too many requests - limit is 5 per second.");
        }
    }
}
