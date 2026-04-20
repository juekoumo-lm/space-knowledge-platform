package com.space.knowledge.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Attempt {
    private Long id;
    private Long userId;
    private Long questionId;
    private Integer levelId;
    private String answer;
    private Integer isCorrect;
    private Integer timeSpent;
    private Integer attemptNo;
    private String source;
    private LocalDateTime createdAt;
}
