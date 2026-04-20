package com.space.knowledge.mapper;

import com.space.knowledge.entity.Grade;
import java.util.List;

public interface GradeMapper {
    List<Grade> selectAll();
    Grade selectById(Integer id);
}
