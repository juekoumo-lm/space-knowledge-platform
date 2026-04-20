package com.space.knowledge.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class WrongQuestion {
    private Long id;
    private Long userId;
    private Long questionId;
    private String note;
    private LocalDateTime addedAt;
    private Question question;
}
