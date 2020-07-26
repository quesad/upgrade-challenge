package com.upgrade.challenge.api.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel(value="Booking")
public class BookingDto {

    @ApiModelProperty(value="Email", dataType = "String", required = true)
    private String email;

    @ApiModelProperty(value="Full name", dataType = "String", required = true)
    private String fullName;

    @ApiModelProperty(value="Check In date", dataType = "String", required = true, example = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate checkInDate;

    @ApiModelProperty(value="check Out date", dataType = "String", required = true, example = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate checkOutDate;
}
