package com.upgrade.challenge.api.service;

import com.upgrade.challenge.api.entity.Booking;
import com.upgrade.challenge.api.entity.ReservedDay;
import com.upgrade.challenge.api.repository.BookingRepository;
import com.upgrade.challenge.api.repository.ReservedDayRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.Matchers.hasSize;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class BookingServiceConcurrencyTest {

    public final int NUMBER_OF_EXECUTIONS = 10;

    @Autowired
    BookingService bookingService;

    @Autowired
    BookingRepository bookingRepository;

    @Autowired
    ReservedDayRepository reservedDayRepository;

    @Test
    public void book_concurrent_should_be_ok() throws InterruptedException {
        final ExecutorService executor = Executors.newFixedThreadPool(NUMBER_OF_EXECUTIONS + 1);

        for (int i = 1; i <= NUMBER_OF_EXECUTIONS; i++) {
            Booking booking = Booking.builder()
                    .email("a@b.c")
                    .fullName("John Doe")
                    .checkInDate(LocalDate.now().plusDays(i))
                    .checkOutDate(LocalDate.now().plusDays(i + 1))
                    .build();
            executor.execute(() -> bookingService.book(booking));
        }

        Booking bookingWithReservedDate = Booking.builder()
                .email("a@b.c")
                .fullName("John Doe")
                .checkInDate(LocalDate.now().plusDays(1))
                .checkOutDate(LocalDate.now().plusDays(3))
                .build();

        executor.execute(() -> bookingService.book(bookingWithReservedDate));

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);

        Assert.assertEquals(bookingRepository.count(), NUMBER_OF_EXECUTIONS);
        Assert.assertEquals(reservedDayRepository.count(), NUMBER_OF_EXECUTIONS);

    }
}
