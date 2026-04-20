package com.space.knowledge.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class Level {
    private Integer id;
    private String name;
    private Integer gradeId;
    private String theme;
    private Integer difficulty;
    private String requiredKps;
    private Integer questionCount;
    private BigDecimal passScore;
    private Integer sortOrder;
    private List<Long> questionIds;
    private String gradeName;
}
