package com.authservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class JsonWrapper<T> {
    private T value;
    private ErrorResponseDto error;

    public JsonWrapper(T value) {
        this.value = value;
        this.error = null;
    }
}