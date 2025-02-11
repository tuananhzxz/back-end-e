package com.shop.ecommerce.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
@RequiredArgsConstructor
public class MessageMultiUtils {

    private final MessageSource messageSource;

    public String getMessage(String key) {
        return messageSource.getMessage(key, null, Locale.getDefault());
    }
}
