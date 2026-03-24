package com.exam.MinhLong_1773.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorForm {

    @NotBlank(message = "Ten bac si khong duoc de trong")
    private String name;

    @NotBlank(message = "Chuyen khoa khong duoc de trong")
    private String specialty;

    private String image;

    @NotNull(message = "Vui long chon khoa")
    private Long departmentId;
}
