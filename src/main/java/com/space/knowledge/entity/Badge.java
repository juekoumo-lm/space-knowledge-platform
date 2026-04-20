package com.space.knowledge.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Badge {
    private Integer id;
    private String name;
    private String conditionType;
    private String conditionValue;
    private String icon;
    private String description;
    /** 当前用户是否已获得，查询时设置 */
    private Boolean obtained;
}
