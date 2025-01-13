package com.authservice.dto;

import java.util.Optional;
import java.util.function.Function;


public class OperationResult<T> {

    private final T value;
    private final String error;

    public OperationResult(T value, String error) {
        this.value = value;
        this.error = error;
    }

    public static <T> OperationResult<T> success(T value) {
        return new OperationResult<>(value, null);
    }

    public static <T> OperationResult<T> failure(String error) {
        return error == null || error.isEmpty()
                ? new OperationResult<>(null, "Error cannot be null or empty")
                : new OperationResult<>(null, error);
    }

    public boolean isSuccess() {
        return error == null;
    }

    public boolean isFailure() {
        return !isSuccess();
    }

    public Optional<T> getValue() {
        return Optional.ofNullable(value);
    }

    public Optional<String> getError() {
        return Optional.ofNullable(error);
    }

    public <U> OperationResult<U> map(Function<T, U> mapper) {
        if (isSuccess()) {
            return OperationResult.success(mapper.apply(value));
        }
        return OperationResult.failure(error);
    }

    public <U> OperationResult<U> flatMap(Function<T, OperationResult<U>> mapper) {
        if (isSuccess()) {
            return mapper.apply(value);
        }
        return OperationResult.failure(error);
    }
}
