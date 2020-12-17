package com.takeaway.demo.gameofthree.exceptions;

import lombok.Getter;

/**
 * PartyException.
 *
 * @author Andrey Arefyev
 */
public class PartyException extends RuntimeException {

    @Getter
    private final transient Object[] messageParams;

    public PartyException(String message, Object... messageParams) {
        super(message);
        this.messageParams = messageParams;
    }

}