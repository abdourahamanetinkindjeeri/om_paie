package com.odc.om.paie.authenticated.auth;

import com.odc.om.paie.authenticated.user.User;
import com.odc.om.paie.authenticated.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OtpService {

    private final UserRepository userRepository;

    public String generateOtp(User user) {
        String otp = String.format("%06d", new SecureRandom().nextInt(999999));
        user.setOtpCode(otp);
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(5));
        userRepository.save(user);
        return otp;
    }

    public boolean validateOtp(String telephone, String otp) {
        return userRepository.findByTelephone(telephone)
                .filter(user -> otp.equals(user.getOtpCode()))
                .filter(user -> user.getOtpExpiry() != null && user.getOtpExpiry().isAfter(LocalDateTime.now()))
                .isPresent();
    }

    public void clearOtp(User user) {
        user.setOtpCode(null);
        user.setOtpExpiry(null);
        userRepository.save(user);
    }

    public void clearOtpByTelephone(String telephone) {
        userRepository.findByTelephone(telephone).ifPresent(this::clearOtp);
    }
}