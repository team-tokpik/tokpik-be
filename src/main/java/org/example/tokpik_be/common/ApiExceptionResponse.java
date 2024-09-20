package org.example.tokpik_be.common;

public record ApiExceptionResponse<T> (
    T message
) {

}
