package it.techn.gumeh.security;

import org.springframework.security.core.AuthenticationException;

/**
 * Created by H_Maghboli on 6/24/2018.
 */
public class NoLoginUserException extends AuthenticationException {

    private static final long serialVersionUID = 1L;

    public NoLoginUserException(String msg, Throwable t) {
        super(msg, t);
    }

    public NoLoginUserException(String message) {
        super(message);
    }
}
