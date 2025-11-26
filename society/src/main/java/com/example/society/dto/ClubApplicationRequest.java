package com.example.society.dto;

import lombok.Data;

@Data
public class ClubApplicationRequest {
    private Integer studentId;
    private Integer clubId;
    private String auditStatus;
}