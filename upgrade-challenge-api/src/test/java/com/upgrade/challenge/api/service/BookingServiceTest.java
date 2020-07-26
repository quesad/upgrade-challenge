package com.upgrade.challenge.api.service;

import com.upgrade.challenge.api.entity.Booking;
import com.upgrade.challenge.api.entity.ReservedDay;
import com.upgrade.challenge.api.exceprions.InvalidDateIntervalException;
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
import java.util.*;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class BookingServiceTest {

    @Autowired
    BookingService bookingService;

    @MockBean
    BookingRepository bookingRepository;

    @MockBean
    ReservedDayRepository reservedDayRepository;

    @Test(expected = IllegalArgumentException.class)
    public void book_without_email_should_throw_exception() {
        Booking booking = Booking.builder()
                .email("")
                .fullName("John Doe")
                .checkInDate(LocalDate.now().plusDays(3))
                .checkOutDate(LocalDate.now().plusDays(4))
                .build();

        bookingService.book(booking);
    }

    @Test(expected = IllegalArgumentException.class)
    public void book_without_fullname_should_throw_exception() {
        Booking booking = Booking.builder()
                .email("a@b.c")
                .fullName("")
                .checkInDate(LocalDate.now().plusDays(3))
                .checkOutDate(LocalDate.now().plusDays(4))
                .build();

        bookingService.book(booking);
    }

    @Test(expected = IllegalArgumentException.class)
    public void book_without_check_in_should_throw_exception() {
        Booking booking = Booking.builder()
                .email("a@b.c")
                .fullName("John Doe")
                .checkInDate(null)
                .checkOutDate(LocalDate.now().plusDays(4))
                .build();

        bookingService.book(booking);
    }

    @Test(expected = IllegalArgumentException.class)
    public void book_without_check_out_should_throw_exception() {
        Booking booking = Booking.builder()
                .email("a@b.c")
                .fullName("John Doe")
                .checkInDate(LocalDate.now().plusDays(3))
                .checkOutDate(null)
                .build();

        bookingService.book(booking);
    }

    @Test(expected = InvalidDateIntervalException.class)
    public void book_with_check_in_after_check_out_should_throw_exception() {
        Booking booking = Booking.builder()
                .email("a@b.c")
                .fullName("John Doe")
                .checkInDate(LocalDate.now().plusDays(6))
                .checkOutDate(LocalDate.now().plusDays(3))
                .build();

        bookingService.book(booking);
    }

    @Test(expected = InvalidDateIntervalException.class)
    public void book_for_more_than_3_days_should_throw_exception() {
        Booking booking = Booking.builder()
                .email("a@b.c")
                .fullName("John Doe")
                .checkInDate(LocalDate.now().plusDays(2))
                .checkOutDate(LocalDate.now().plusDays(8))
                .build();

        bookingService.book(booking);
    }

    @Test(expected = InvalidDateIntervalException.class)
    public void book_for_today_should_throw_exception() {
        Booking booking = Booking.builder()
                .email("a@b.c")
                .fullName("John Doe")
                .checkInDate(LocalDate.now())
                .checkOutDate(LocalDate.now().plusDays(1))
                .build();

        bookingService.book(booking);
    }

    @Test(expected = InvalidDateIntervalException.class)
    public void book_with_more_than_month_ahead_should_throw_exception() {
        Booking booking = Booking.builder()
                .email("a@b.c")
                .fullName("John Doe")
                .checkInDate(LocalDate.now().plusDays(45))
                .checkOutDate(LocalDate.now().plusDays(46))
                .build();

        bookingService.book(booking);
    }

    @Test
    public void update_email_only_email_should_change() {
        Long id = new Random().nextLong();
        String email = "fake-old@old.com";
        String fullName = "John Doe";
        LocalDate checkIn = LocalDate.now().plusDays(1);
        LocalDate checkOut = LocalDate.now().plusDays(3);

        String newEmail = "fake-new@new.com";

        Booking booking = Booking.builder()
                .id(id)
                .email(newEmail)
//                .fullName(bookingDto.getFullName())
//                .checkInDate(bookingDto.getCheckInDate())
//                .checkOutDate(bookingDto.getCheckOutDate())
                .build();

        Booking currBooking = Booking.builder()
                .id(id)
                .email(email)
                .fullName(fullName)
                .checkInDate(checkIn)
                .checkOutDate(checkOut)
                .build();


        when(bookingRepository.findById(any())).thenReturn(Optional.of(currBooking));

        when(bookingRepository.save(any())).thenReturn(currBooking);

        currBooking = bookingService.update(booking);

        Assert.assertEquals(currBooking.getId(), id);
        Assert.assertEquals(currBooking.getEmail(), newEmail);
        Assert.assertEquals(currBooking.getFullName(), fullName);
        Assert.assertEquals(currBooking.getCheckInDate(), checkIn);
        Assert.assertEquals(currBooking.getCheckOutDate(), checkOut);
    }

    @Test
    public void update_fullname_only_fullname_should_change() {
        Long id = new Random().nextLong();
        String email = "fake-old@old.com";
        String fullName = "John Doe";
        LocalDate checkIn = LocalDate.now().plusDays(1);
        LocalDate checkOut = LocalDate.now().plusDays(3);

        String newFullname = "Another Doe";

        Booking booking = Booking.builder()
                .id(id)
//                .email(newEmail)
                .fullName(newFullname)
//                .checkInDate(bookingDto.getCheckInDate())
//                .checkOutDate(bookingDto.getCheckOutDate())
                .build();

        Booking currBooking = Booking.builder()
                .id(id)
                .email(email)
                .fullName(fullName)
                .checkInDate(checkIn)
                .checkOutDate(checkOut)
                .build();


        when(bookingRepository.findById(any())).thenReturn(Optional.of(currBooking));

        when(bookingRepository.save(any())).thenReturn(currBooking);

        currBooking = bookingService.update(booking);

        Assert.assertEquals(currBooking.getId(), id);
        Assert.assertEquals(currBooking.getEmail(), email);
        Assert.assertEquals(currBooking.getFullName(), newFullname);
        Assert.assertEquals(currBooking.getCheckInDate(), checkIn);
        Assert.assertEquals(currBooking.getCheckOutDate(), checkOut);
    }

    @Test
    public void findAvailableDates_2_reserved_days_of_next_10_days() {
        LocalDate firstReservedDate = LocalDate.now().plusDays(1);
        LocalDate secondReservedDay = LocalDate.now().plusDays(2);
        ReservedDay reservedDay = ReservedDay.builder()
                .date(firstReservedDate)
                .build();

        List<ReservedDay> reservedDays = new ArrayList<>();
        reservedDays.add(reservedDay);

        reservedDay = ReservedDay.builder()
                .date(secondReservedDay)
                .build();

        reservedDays.add(reservedDay);

        when(reservedDayRepository.findReservedDaysBetweenDates(any(), any())).thenReturn(reservedDays);

        Set<LocalDate> availableDays = bookingService.findAvailableDates(LocalDate.now(), LocalDate.now().plusDays(10));

        Assert.assertThat(availableDays, hasSize(9));
        Assert.assertFalse(availableDays.contains(firstReservedDate));
        Assert.assertFalse(availableDays.contains(secondReservedDay));

    }
}
