package com.shop.ecommerce.utils;

import java.util.Random;

public class OtpUtils {

    public static String generateOtp() {
        int optLength = 6;

        Random random = new Random();

        StringBuilder otp = new StringBuilder();

        for (int i = 0; i < optLength; i++) {
            otp.append(random.nextInt(10));
        }

        return otp.toString();
    }
}
