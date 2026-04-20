package com.space.knowledge.entity;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class Question {
    public static final String TYPE_SINGLE = "SINGLE";
    public static final String TYPE_MULTIPLE = "MULTIPLE";
    public static final String TYPE_JUDGE = "JUDGE";
    public static final String TYPE_FILL = "FILL";
    public static final String TYPE_SUBJECTIVE = "SUBJECTIVE";

    private Long id;
    @NotBlank(message = "题干不能为空")
    @Size(max = 1000, message = "题干长度不能超过1000字符")
    private String content;
    @NotBlank(message = "题型不能为空")
    private String type;
    @NotNull(message = "难度不能为空")
    private Integer difficulty;
    private Integer gradeId;
    private Long teacherId;
    @Size(max = 2000, message = "解析长度不能超过2000字符")
    private String analysis;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer status;
    private List<QuestionOption> options;
    private List<Integer> kpIds;
    private List<String> tags;
}
