package com.my.fomo.chart.infrastructure;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * KIS OpenAPI 초당 10건 제한 대응 슬라이딩 윈도우 큐.
 * 단일 스레드 executor로 직렬 실행하며, 1초 윈도우 내 최대 5건을 보장한다.
 */
@Slf4j
@Component
public class KisRequestQueue {

    private static final int MAX_PER_SECOND = 5;
    private static final long WINDOW_MS = 1000;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    // 단일 스레드 executor 안에서만 접근하므로 동기화 불필요
    private final Deque<Long> requestLog = new ArrayDeque<>();

    public <T> T submit(Callable<T> task) {
        try {
            return executor.submit(() -> {
                waitForPermit();
                return task.call();
            }).get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("KIS 요청 인터럽트", e);
        } catch (ExecutionException e) {
            throw new RuntimeException("KIS 요청 실패", e.getCause());
        }
    }

    private void waitForPermit() throws InterruptedException {
        while (true) {
            long now = System.currentTimeMillis();
            long windowStart = now - WINDOW_MS;

            // 1초 밖으로 벗어난 타임스탬프 제거
            while (!requestLog.isEmpty() && requestLog.peekFirst() <= windowStart) {
                requestLog.pollFirst();
            }

            if (requestLog.size() < MAX_PER_SECOND) {
                requestLog.addLast(now);
                return;
            }

            // 윈도우 내 요청이 꽉 찼으면 가장 오래된 요청이 만료될 때까지 대기
            long sleepMs = requestLog.peekFirst() + WINDOW_MS - now + 1;
            Thread.sleep(Math.max(1, sleepMs));
        }
    }

    @PreDestroy
    public void shutdown() {
        executor.shutdown();
    }
}
