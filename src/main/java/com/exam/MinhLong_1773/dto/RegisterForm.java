package com.exam.MinhLong_1773.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterForm {

    @NotBlank(message = "Username khong duoc de trong")
    @Size(min = 3, max = 100, message = "Username tu 3 den 100 ky tu")
    private String username;

    @NotBlank(message = "Password khong duoc de trong")
    @Size(min = 6, max = 100, message = "Password toi thieu 6 ky tu")
    private String password;

    @NotBlank(message = "Email khong duoc de trong")
    @Email(message = "Email khong hop le")
    private String email;
}
