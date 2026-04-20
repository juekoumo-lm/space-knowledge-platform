package com.space.knowledge.mapper;

import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface LevelQuestionMapper {
    int insert(@Param("levelId") Integer levelId, @Param("questionId") Long questionId);
    int insertBatch(@Param("levelId") Integer levelId, @Param("questionIds") List<Long> questionIds);
    int deleteByLevelId(Integer levelId);
}
