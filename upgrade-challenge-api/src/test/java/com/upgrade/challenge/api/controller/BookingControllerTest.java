package com.upgrade.challenge.api.controller;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.upgrade.challenge.api.dto.BookingDto;
import com.upgrade.challenge.api.entity.Booking;
import com.upgrade.challenge.api.service.BookingService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.IOException;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BookingControllerTest {

    MockMvc mockMvc;

    @Autowired
    BookingController bookingController;

    @MockBean
    BookingService bookingService;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(bookingController)
                .build();
    }

    private MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));

    private byte[] convertObjectToJsonBytes(Object object) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.registerModule(new JavaTimeModule());
        return mapper.writeValueAsBytes(object);
    }

    @Test
    public void with_no_reservation_should_return_all_dates_available() throws Exception {
        LocalDate startDate = LocalDate.now().plusDays(10);
        Set<LocalDate> availableDates = Stream.iterate(startDate, date -> date.plusDays(1))
                .limit(10)
                .collect(Collectors.toSet());
        when(bookingService.findAvailableDates(any(), any())).thenReturn(availableDates);

        mockMvc.perform(get("/api/booking/available")
//                .param("startDate", "2020-07-26")
                .param("startDate", startDate.toString())
                .param("endDate", LocalDate.now().plusMonths(2).toString()))
                //Assert
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(10)));
    }

    @Test
    public void givenValidInputShouldReturnBookingWhenBooking() throws Exception {
        //Arrange
        BookingDto bookingDto = BookingDto.builder()
                .email("a@b.c")
                .fullName("John Doe")
                .checkInDate(LocalDate.now().plusDays(1))
                .checkOutDate(LocalDate.now().plusDays(3))
                .build();

        Booking booking = Booking.builder()
                .id(new Random().nextLong())
                .email(bookingDto.getEmail())
                .fullName(bookingDto.getFullName())
                .checkInDate(bookingDto.getCheckInDate())
                .checkOutDate(bookingDto.getCheckOutDate())
                .build();

        when(bookingService.book(any())).thenReturn(booking);
        //Act
        mockMvc.perform(post("/api/booking")
                .content(this.convertObjectToJsonBytes(bookingDto))
                .contentType(this.APPLICATION_JSON_UTF8))
                //Assert
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(not(empty()))))
                .andExpect(jsonPath("$", is(booking.getId())));
    }

//    @Test
//    public void with_a_1_day_reservation_should_return_all_dates_available() throws Exception {
//        Booking booking = Booking.builder()
//                .email("a@b.c")
//                .fullName("John Doe")
//                .checkInDate(LocalDate.now().plusDays(3))
//                .checkOutDate(LocalDate.now().plusDays(4))
//                .build();
//
//        bookingService.book(booking);
//
//        mockMvc.perform(get("/api/booking/available")
//                .param("startDate", "2020-07-26")
//                .param("endDate", "2020-08-04"))
//                //Assert
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$", hasSize(9)));
//    }

}
