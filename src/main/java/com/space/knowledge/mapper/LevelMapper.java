package com.space.knowledge.mapper;

import com.space.knowledge.entity.Level;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface LevelMapper {
    Level selectById(Integer id);
    List<Level> selectByGradeId(Integer gradeId);
    List<Integer> selectQuestionIdsByLevelId(Integer levelId);
    int insert(Level level);
    int update(Level level);
    int delete(Integer id);
}
