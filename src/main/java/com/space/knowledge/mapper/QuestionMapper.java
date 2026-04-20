package com.space.knowledge.mapper;

import com.space.knowledge.entity.Question;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface QuestionMapper {
    Question selectById(Long id);
    List<Question> selectByCondition(@Param("gradeId") Integer gradeId, @Param("kpId") Integer kpId,
                                    @Param("difficulty") Integer difficulty, @Param("type") String type,
                                    @Param("keyword") String keyword, @Param("offset") int offset, @Param("limit") int limit);
    int countByCondition(@Param("gradeId") Integer gradeId, @Param("kpId") Integer kpId,
                         @Param("difficulty") Integer difficulty, @Param("type") String type, @Param("keyword") String keyword);
    int insert(Question question);
    int update(Question question);
    int deleteById(Long id);
    List<Question> selectByIds(@Param("ids") List<Long> ids);
}
