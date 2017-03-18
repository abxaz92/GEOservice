package ru.macrobit.geoservice.common.exception;

/**
 * Created by [david] on 18.03.17.
 */
public class InvalidRequestException extends Exception {
    public InvalidRequestException() {
    }

    public InvalidRequestException(String message) {
        super(message);
    }
}
