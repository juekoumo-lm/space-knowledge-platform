package com.space.knowledge.mapper;

import com.space.knowledge.entity.Clazz;

import java.util.List;

public interface ClazzMapper {

    List<Clazz> selectByGradeId(Integer gradeId);

    Clazz selectById(Integer id);

    void insert(Clazz clazz);

    void update(Clazz clazz);

    void delete(Integer id);
}
