package com.TimeLuxWatchBE.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Custom exception for cases where creating or updating a resource
 * would result in a duplicate entry (e.g., based on a unique constraint like name).
 */
@ResponseStatus(HttpStatus.CONFLICT) // Returns a 409 Conflict status
public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(String message) {
        super(message);
    }

    public DuplicateResourceException(String message, Throwable cause) {
        super(message, cause);
    }
} 