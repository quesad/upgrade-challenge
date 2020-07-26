package com.upgrade.challenge.api.exceprions;

public class BookingAlreadyFinishedException extends RuntimeException {

    public BookingAlreadyFinishedException() {
        super("Booking already finished.");
    }
}
