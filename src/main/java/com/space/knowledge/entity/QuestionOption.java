package com.space.knowledge.entity;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class QuestionOption {
    private Long id;
    private Long questionId;
    @NotBlank(message = "选项标签不能为空")
    @Size(max = 8, message = "选项标签长度不能超过8字符")
    private String optionLabel;
    @NotBlank(message = "选项内容不能为空")
    @Size(max = 500, message = "选项内容长度不能超过500字符")
    private String optionText;
    @NotNull(message = "是否正确不能为空")
    private Integer isCorrect;
}
