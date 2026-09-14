package com.sentinelscm.service;

/** Thrown when a requested entity does not exist. Mapped to HTTP 404 at the web boundary. */
public class NotFoundException extends RuntimeException {

    public NotFoundException(String entity, Object id) {
        super(entity + " " + id + " not found");
    }
}
