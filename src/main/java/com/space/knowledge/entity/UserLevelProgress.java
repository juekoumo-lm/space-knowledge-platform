package com.space.knowledge.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class UserLevelProgress {
    private Long id;
    private Long userId;
    private Integer levelId;
    private BigDecimal score;
    private Integer passed;
    private Integer bestTimeSpent;
    private LocalDateTime completedAt;
}
