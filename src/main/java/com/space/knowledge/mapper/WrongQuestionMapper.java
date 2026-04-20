package com.space.knowledge.mapper;

import com.space.knowledge.entity.WrongQuestion;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface WrongQuestionMapper {
    int insert(WrongQuestion wq);
    int delete(@Param("userId") Long userId, @Param("questionId") Long questionId);
    List<WrongQuestion> selectByUserId(@Param("userId") Long userId, @Param("offset") int offset, @Param("limit") int limit);
    int countByUserId(Long userId);
    WrongQuestion selectOne(@Param("userId") Long userId, @Param("questionId") Long questionId);
}
