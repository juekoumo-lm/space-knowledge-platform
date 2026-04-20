package com.space.knowledge.mapper;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface StatisticsMapper {
    List<Map<String, Object>> levelPassRateByGrade(@Param("gradeId") Integer gradeId);
    List<Map<String, Object>> studentActiveRank(@Param("classId") Integer classId, @Param("limit") int limit);
    List<Map<String, Object>> wrongQuestionHot(@Param("gradeId") Integer gradeId, @Param("limit") int limit);
    List<Map<String, Object>> kpMasteryHeatmap(@Param("classId") Integer classId);
}
