package com.odc.om.paie.authenticated.auth;

import jakarta.validation.constraints.NotBlank;

public class OtpVerificationRequest {
    @NotBlank(message = "Telephone is required")
    private String telephone;
    @NotBlank(message = "OTP code is required")
    private String otp;

    public OtpVerificationRequest() {}

    public OtpVerificationRequest(String telephone, String otp) {
        this.telephone = telephone;
        this.otp = otp;
    }

    public String telephone() {
        return telephone;
    }

    public String otp() {
        return otp;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }
}