package com.exam.MinhLong_1773.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentForm {

    @NotNull(message = "Vui long chon ngay kham")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate appointmentDate;
}
