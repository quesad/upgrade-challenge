package com.upgrade.challenge.api.exceprions;

public class BookingDateUnavailableException extends RuntimeException {

    public BookingDateUnavailableException() {
        super("Booking date is unavailable.");
    }
}
