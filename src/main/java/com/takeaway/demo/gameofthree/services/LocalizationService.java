package com.takeaway.demo.gameofthree.services;

/**
 * LocalizationService.
 *
 * @author Andrey Arefyev
 */
public interface LocalizationService {
    String getMessage(String message, Object... messageParams);
}