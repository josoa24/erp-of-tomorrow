package com.hackaton.auth.common.exception;

public class AccountDisabledException extends RuntimeException {

    public AccountDisabledException() {
        super("Ce compte est désactivé");
    }
}
