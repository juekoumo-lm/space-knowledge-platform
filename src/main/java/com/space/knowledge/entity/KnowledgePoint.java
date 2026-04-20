package com.space.knowledge.entity;

import lombok.Data;
import java.util.List;

@Data
public class KnowledgePoint {
    private Integer id;
    private String name;
    private Integer parentId;
    private Integer gradeLevel;
    private String description;
    private Integer sortOrder;
    private List<KnowledgePoint> children;
}
