package com.space.knowledge.mapper;

import com.space.knowledge.entity.QuestionOption;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface QuestionOptionMapper {
    List<QuestionOption> selectByQuestionId(Long questionId);
    List<QuestionOption> selectByQuestionIds(@Param("list") List<Long> questionIds);
    int insert(QuestionOption option);
    int insertBatch(@Param("list") List<QuestionOption> list);
    int deleteByQuestionId(Long questionId);
}
