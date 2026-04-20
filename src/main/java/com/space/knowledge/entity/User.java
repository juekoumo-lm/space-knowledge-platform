package com.space.knowledge.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class User {
    private Long id;
    private String username;
    private String passwordHash;
    private Integer roleId;
    private String realName;
    private Integer gradeId;
    private Integer classId;
    private String avatar;
    private Integer points;
    private LocalDateTime createTime;
    private LocalDateTime lastLogin;

    public boolean isStudent() { return roleId != null && roleId == 1; }
    public boolean isTeacher() { return roleId != null && roleId == 2; }
    public boolean isAdmin() { return roleId != null && roleId == 3; }
}
