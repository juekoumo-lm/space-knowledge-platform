package com.space.knowledge.mapper;

import com.space.knowledge.entity.Assignment;

import java.util.List;

public interface AssignmentMapper {

    List<Assignment> selectByTeacherId(Long teacherId);

    List<Assignment> selectByClassId(Integer classId);

    Assignment selectById(Long id);

    void insert(Assignment assignment);

    void update(Assignment assignment);

    void delete(Long id);
}
