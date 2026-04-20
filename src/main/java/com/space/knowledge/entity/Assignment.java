package com.space.knowledge.entity;

import java.time.LocalDateTime;

public class Assignment {
    private Long id;
    private Long teacherId;
    private Integer classId;
    private Integer levelId;
    private String questionIds;
    private String title;
    private LocalDateTime dueAt;
    private LocalDateTime createdAt;

    public Assignment() {
    }

    public Assignment(Long id, Long teacherId, Integer classId, Integer levelId, String questionIds, String title, LocalDateTime dueAt, LocalDateTime createdAt) {
        this.id = id;
        this.teacherId = teacherId;
        this.classId = classId;
        this.levelId = levelId;
        this.questionIds = questionIds;
        this.title = title;
        this.dueAt = dueAt;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Long teacherId) {
        this.teacherId = teacherId;
    }

    public Integer getClassId() {
        return classId;
    }

    public void setClassId(Integer classId) {
        this.classId = classId;
    }

    public Integer getLevelId() {
        return levelId;
    }

    public void setLevelId(Integer levelId) {
        this.levelId = levelId;
    }

    public String getQuestionIds() {
        return questionIds;
    }

    public void setQuestionIds(String questionIds) {
        this.questionIds = questionIds;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDateTime getDueAt() {
        return dueAt;
    }

    public void setDueAt(LocalDateTime dueAt) {
        this.dueAt = dueAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Assignment{" +
                "id=" + id +
                ", teacherId=" + teacherId +
                ", classId=" + classId +
                ", levelId=" + levelId +
                ", questionIds='" + questionIds + '\'' +
                ", title='" + title + '\'' +
                ", dueAt=" + dueAt +
                ", createdAt=" + createdAt +
                '}';
    }
}