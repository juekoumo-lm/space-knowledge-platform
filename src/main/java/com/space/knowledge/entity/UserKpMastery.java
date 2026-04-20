package com.space.knowledge.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class UserKpMastery {
    private Long userId;
    private Integer kpId;
    private BigDecimal masteryScore;
    private LocalDateTime lastUpdate;
}
