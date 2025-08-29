package com.yourname.bajajchallenge.dto;

import lombok.Data;

@Data // Using Lombok to generate getters and setters
public class WebhookRequest {
    private String name;
    private String regNo;
    private String email;
}