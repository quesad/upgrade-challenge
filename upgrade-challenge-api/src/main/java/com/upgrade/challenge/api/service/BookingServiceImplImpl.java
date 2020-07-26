package com.upgrade.challenge.api.service;

import com.upgrade.challenge.api.entity.Booking;
import com.upgrade.challenge.api.entity.ReservedDay;
import com.upgrade.challenge.api.exceprions.BookingAlreadyFinishedException;
import com.upgrade.challenge.api.exceprions.InvalidDateIntervalException;
import com.upgrade.challenge.api.repository.BookingRepository;
import com.upgrade.challenge.api.repository.ReservedDayRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class BookingServiceImplImpl implements BookingService {

    private BookingRepository bookingRepository;

    private ReservedDayRepository reservedDayRepository;

    public BookingServiceImplImpl(BookingRepository bookingRepository, ReservedDayRepository reservedDayRepository) {
        this.bookingRepository = bookingRepository;
        this.reservedDayRepository = reservedDayRepository;
    }

    private void validateDateRange(LocalDate checkIn, LocalDate checkOut) {
        if (checkIn == null || checkOut == null) {
            throw new IllegalArgumentException("Booking dates are required.");
        }

        if (!checkIn.isBefore(checkOut)) {
            throw new InvalidDateIntervalException("Check In date should be before Check Out");
        }

        if (ChronoUnit.DAYS.between(checkIn, checkOut) > 3) {
            throw new InvalidDateIntervalException("The campsite can be reserved for max 3 days.");
        }

        long daysBetweenNowAndCheckIn = ChronoUnit.DAYS.between(LocalDate.now(), checkIn);
        if (daysBetweenNowAndCheckIn < 1 || daysBetweenNowAndCheckIn > 30) {
            throw new InvalidDateIntervalException("The campsite can be reserved minimum 1 day ahead of arrival and up to 1 month in advance.");
        }
    }

    private void validateBooking(Booking booking) {
        this.validateDateRange(booking.getCheckInDate(), booking.getCheckOutDate());

        if (booking.getEmail() == null || booking.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("The e-mail is required to complete the reservation.");
        }

        if (booking.getFullName() == null || booking.getFullName().trim().isEmpty()) {
            throw new IllegalArgumentException("The full name is required to complete the reservation.");
        }
    }

    private Booking getValidBookingById(Long bookingId) {
        if (bookingId == null) {
            throw new IllegalArgumentException("An reservation Id is required to cancel!");
        }
        Optional<Booking> booking = bookingRepository.findById(bookingId);
        Booking elem = booking.orElseThrow(() -> new IllegalArgumentException("Invalid reservation Id!"));
        if (elem.getCheckOutDate().isBefore(LocalDate.now())) {
            throw new BookingAlreadyFinishedException();
        }

        return elem;
    }

    @Override
    public Set<LocalDate> findAvailableDates(LocalDate initialDate, LocalDate endDate) {
        initialDate = initialDate == null || initialDate.isBefore(LocalDate.now()) ? LocalDate.now() : initialDate;
        endDate = endDate == null ? LocalDate.now().plusMonths(1) : endDate;
        long daysBetween = ChronoUnit.DAYS.between(initialDate, endDate) + 1;
        Set<LocalDate> availableDates = Stream.iterate(initialDate, date -> date.plusDays(1))
                .limit(daysBetween < 0? 0 : daysBetween)
                .collect(Collectors.toSet());

        if (availableDates.isEmpty()) {
            return availableDates;
        }

        List<ReservedDay> reservedDays = this.reservedDayRepository.findReservedDaysBetweenDates(initialDate, endDate);
        for (ReservedDay reservedDay : reservedDays) {
            availableDates.remove(reservedDay.getDate());
        }

        return availableDates;
    }

    @Override
    @Transactional
    public Booking book(Booking booking) {

        this.validateBooking(booking);

        for (LocalDate date = booking.getCheckInDate(); date.isBefore(booking.getCheckOutDate()); date = date.plusDays(1)) {
            reservedDayRepository.save(ReservedDay.builder()
                    .date(date).build());
        }

        return bookingRepository.save(booking);
    }

    @Override
    @Transactional
    public Booking update(Booking booking) {
        Booking currentBooking = this.getValidBookingById(booking.getId());

        if (booking.getEmail() != null) {
            currentBooking.setEmail(booking.getEmail());
        }

        if (booking.getFullName() != null) {
            currentBooking.setFullName(booking.getFullName());
        }

        if (booking.getCheckInDate() != null || booking.getCheckOutDate() != null) {
            LocalDate currentCheckIn = currentBooking.getCheckInDate();
            LocalDate currentCheckOut = currentBooking.getCheckOutDate();

            currentBooking.setCheckInDate(
                    booking.getCheckInDate() != null ? booking.getCheckInDate() : currentCheckIn);
            currentBooking.setCheckOutDate(
                    booking.getCheckOutDate() != null ? booking.getCheckOutDate() : currentCheckOut);

            this.validateBooking(currentBooking);

            Optional<ReservedDay> reservedDay;
            ReservedDay reserved;
            for (LocalDate date = currentCheckIn; date.isBefore(currentCheckOut); date = date.plusDays(1)) {
                if (!date.isBefore(currentBooking.getCheckInDate()) && date.isBefore(currentBooking.getCheckOutDate()))
                    continue;

                reservedDay = reservedDayRepository.findByDate(date);
                reserved = reservedDay.orElseThrow(() -> new IllegalArgumentException("Invalid Date"));
                reservedDayRepository.delete(reserved);
            }

            for (LocalDate date = currentBooking.getCheckInDate(); date.isBefore(currentBooking.getCheckOutDate()); date = date.plusDays(1)) {
                if (!date.isBefore(currentCheckIn) && date.isBefore(currentCheckOut))
                    continue;

                reservedDayRepository.save(ReservedDay.builder()
                        .date(date).build());
            }
        } else {
            this.validateBooking(currentBooking);
        }

        return bookingRepository.save(currentBooking);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void cancel(Long bookingId) {
        Booking booking = this.getValidBookingById(bookingId);

        Optional<ReservedDay> reservedDay;
        ReservedDay reserved;
        for (LocalDate date = booking.getCheckInDate(); date.isBefore(booking.getCheckOutDate()); date = date.plusDays(1)) {
            reservedDay = reservedDayRepository.findByDate(date);
            reserved = reservedDay.orElseThrow(() -> new IllegalArgumentException("Invalid Date"));
            reservedDayRepository.delete(reserved);
        }

        bookingRepository.delete(booking);
    }
}
