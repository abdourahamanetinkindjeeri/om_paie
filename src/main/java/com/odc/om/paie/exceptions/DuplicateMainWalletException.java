package com.odc.om.paie.exceptions;

public class DuplicateMainWalletException extends RuntimeException {
    public DuplicateMainWalletException(String message) {
        super(message);
    }

    public DuplicateMainWalletException(String message, Throwable cause) {
        super(message, cause);
    }
}