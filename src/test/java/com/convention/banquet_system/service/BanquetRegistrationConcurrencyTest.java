package com.convention.banquet_system.service;

import com.convention.banquet_system.auth.BanquetPolicy;
import com.convention.banquet_system.auth.LoginMember;
import com.convention.banquet_system.domain.Role;
import com.convention.banquet_system.domain.Venue;
import com.convention.banquet_system.dto.BanquetCreateRequest;
import com.convention.banquet_system.exception.BusinessException;
import com.convention.banquet_system.repository.BanquetRepository;
import com.convention.banquet_system.repository.VenueRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class BanquetRegistrationConcurrencyTest {

    @Autowired
    private BanquetRepository banquetRepository;
    @Autowired
    private BanquetPolicy banquetPolicy;
    @Autowired
    private VenueRepository venueRepository;
    @Autowired
    private BanquetService banquetService;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @AfterEach
    void afterEach() {
        jdbcTemplate.update(
                "DELETE FROM banquet WHERE banquet_name = 'concurrencyTest'");
    }

    @Test
    void 동시요청_10개() throws InterruptedException {

        BanquetCreateRequest createRequest = new BanquetCreateRequest(
                "concurrencyTest",
                LocalDate.of(2026, 10, 25),
                LocalTime.of(11, 20),
                LocalTime.of(12, 00),
                null,
                Venue.MAJESTIC_BALLROOM,
                200
        );
        LoginMember loginMember = new LoginMember(1L, Role.PROMOTER);

        int threads = 10;
        ExecutorService pool = Executors.newFixedThreadPool(10);
        var ready = new CountDownLatch(threads);
        var start = new CountDownLatch(1);
        var done = new CountDownLatch(threads);
        var success = new AtomicInteger(0);

        for (int i = 0; i < threads; i++) {
            pool.submit(() -> {
                ready.countDown();
                try {
                    start.await();
                    banquetService.registerBanquet(createRequest, loginMember);
                    success.incrementAndGet(); //등록이 성공하면 여기까지 내려옴
                } catch (BusinessException | InterruptedException e) {
                    System.out.println(e.toString());
                } finally {
                    done.countDown();
                }
            });
        }
        ready.await();
        start.countDown();
        boolean result = done.await(10, TimeUnit.SECONDS);

        pool.shutdown();
        assertThat(success.get()).isEqualTo(1);
        assertThat(result).isTrue();

    }


}
