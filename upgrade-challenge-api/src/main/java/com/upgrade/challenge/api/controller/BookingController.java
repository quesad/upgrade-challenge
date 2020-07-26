package com.upgrade.challenge.api.controller;

import com.upgrade.challenge.api.dto.BookingDto;
import com.upgrade.challenge.api.entity.Booking;
import com.upgrade.challenge.api.entity.ReservedDay;
import com.upgrade.challenge.api.exceprions.BookingDateUnavailableException;
import com.upgrade.challenge.api.repository.BookingRepository;
import com.upgrade.challenge.api.repository.ReservedDayRepository;
import com.upgrade.challenge.api.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.AbstractMap;

@RestController
@CrossOrigin
@RequestMapping(path = "/api/booking")
public class BookingController {

    private BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping(path = "/available")
    public Iterable<LocalDate> getAvailableDates(
            @RequestParam(value = "startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(value = "endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {

        return this.bookingService.findAvailableDates(startDate, endDate);

    }

    @PostMapping
    public ResponseEntity<Long> book(@RequestBody BookingDto bookingDto) {

        Booking booking = Booking.builder()
                .email(bookingDto.getEmail())
                .fullName(bookingDto.getFullName())
                .checkInDate(bookingDto.getCheckInDate())
                .checkOutDate(bookingDto.getCheckOutDate())
                .build();

        try {
            booking = this.bookingService.book(booking);
        } catch (DataIntegrityViolationException ex) {
            throw new BookingDateUnavailableException();
        }

        return ResponseEntity.ok(booking.getId());
    }

    @PutMapping(path = "/{bookingId}")
    @ResponseStatus(value = HttpStatus.OK)
    public void update(@RequestBody BookingDto bookingDto, @PathVariable(name = "bookingId", required = true) Long bookingId) {

        Booking booking = Booking.builder()
                .id(bookingId)
                .email(bookingDto.getEmail())
                .fullName(bookingDto.getFullName())
                .checkInDate(bookingDto.getCheckInDate())
                .checkOutDate(bookingDto.getCheckOutDate())
                .build();

        try {
            this.bookingService.update(booking);
        } catch (DataIntegrityViolationException ex) {
            throw new BookingDateUnavailableException();
        }
    }

    @DeleteMapping(path = "/{bookingId}")
    @ResponseStatus(value = HttpStatus.OK)
    public void cancel(@PathVariable(name = "bookingId", required = true) Long bookingId) {
        this.bookingService.cancel(bookingId);
    }

    /**
     * Global Exception handler for all exceptions.
     */
    @ExceptionHandler({Exception.class})
    public ResponseEntity<AbstractMap.SimpleEntry<String, String>> handle(Exception exception) {
        AbstractMap.SimpleEntry<String, String> response =
                new AbstractMap.SimpleEntry<>("message", exception.getMessage());
        exception.printStackTrace();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }


}
