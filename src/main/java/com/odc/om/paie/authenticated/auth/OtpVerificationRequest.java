package com.odc.om.paie.authenticated.auth;

import jakarta.validation.constraints.NotBlank;

public record OtpVerificationRequest(
        @NotBlank(message = "Telephone is required")
        String telephone,
        @NotBlank(message = "OTP code is required")
        String otp
) {}