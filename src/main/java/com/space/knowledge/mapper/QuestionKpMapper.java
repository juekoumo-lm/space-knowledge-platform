package com.space.knowledge.mapper;

import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface QuestionKpMapper {
    int insert(@Param("questionId") Long questionId, @Param("kpId") Integer kpId);
    int insertBatch(@Param("questionId") Long questionId, @Param("kpIds") List<Integer> kpIds);
    int deleteByQuestionId(Long questionId);
}
