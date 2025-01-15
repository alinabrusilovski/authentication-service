package com.authservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Value;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class EmailMessage implements Serializable {
    private String email;
    private String subject;
    private String body;
    private String publisherType;
}