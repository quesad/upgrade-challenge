package com.upgrade.challenge.api.service;

import com.upgrade.challenge.api.entity.Booking;

import java.time.LocalDate;
import java.util.Set;

public interface BookingService {

    Set<LocalDate> findAvailableDates(LocalDate initialDate, LocalDate endDate);

    Booking book(Booking booking);

    Booking update(Booking booking);

    void cancel(Long bookingId);
}
