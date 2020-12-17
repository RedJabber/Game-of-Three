package com.takeaway.demo.gameofthree.exceptions;

/**
 * PartyNotFoundException.
 *
 * @author Andrey Arefyev
 */
public class PartyNotFoundException extends PartyException {

    public PartyNotFoundException(String message, Object... messageParams) {
        super(message, messageParams);
    }
}