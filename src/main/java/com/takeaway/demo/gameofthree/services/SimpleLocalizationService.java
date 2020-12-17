package com.takeaway.demo.gameofthree.services;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Locale;

/**
 * SimpleLocalizationService.
 *
 * @author Andrey Arefyev
 */
@Component
public class SimpleLocalizationService implements LocalizationService {
    private final MessageSource messageSource;

    public SimpleLocalizationService(MessageSource messageSource) {this.messageSource = messageSource;}

    @Override
    public String getMessage(String messageCode, Object... args) {
        Locale locale = Locale.getDefault();
        var messageVariables = Arrays.copyOf(args, args.length, String[].class);
        return messageSource.getMessage(messageCode, messageVariables, messageCode, locale);
    }
}