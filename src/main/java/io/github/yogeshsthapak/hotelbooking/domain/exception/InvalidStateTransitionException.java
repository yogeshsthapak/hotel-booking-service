package io.github.yogeshsthapak.hotelbooking.domain.exception;

public class InvalidStateTransitionException extends DomainException {
    public InvalidStateTransitionException(String message) {
        super(message);
    }
}
