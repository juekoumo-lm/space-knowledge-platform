package com.space.knowledge.mapper;

import com.space.knowledge.entity.KnowledgePoint;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface KnowledgePointMapper {
    KnowledgePoint selectById(Integer id);
    List<KnowledgePoint> selectByParentId(Integer parentId);
    List<KnowledgePoint> selectAll();
    int insert(KnowledgePoint kp);
    int update(KnowledgePoint kp);
    int delete(Integer id);
    List<Integer> selectKpIdsByQuestionId(Long questionId);
    List<Map<String, Object>> selectKpIdsByQuestionIds(@Param("list") List<Long> questionIds);
}
