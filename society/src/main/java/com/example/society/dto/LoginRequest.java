// 创建独立的 DTO 类
package com.example.society.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String leaderNum;
    private String password;
}