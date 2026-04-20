package com.space.knowledge.service;

import com.space.knowledge.entity.Assignment;
import com.space.knowledge.mapper.AssignmentMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
public class AssignmentService {

    private static final Logger logger = LoggerFactory.getLogger(AssignmentService.class);

    @Resource
    private AssignmentMapper assignmentMapper;

    public List<Assignment> listByTeacher(Long teacherId) {
        logger.debug("获取教师布置的作业列表, teacherId: {}", teacherId);
        return assignmentMapper.selectByTeacherId(teacherId);
    }

    public List<Assignment> listByClass(Integer classId) {
        logger.debug("获取班级的作业列表, classId: {}", classId);
        return assignmentMapper.selectByClassId(classId);
    }

    public Assignment getById(Long id) {
        logger.debug("获取作业详情, id: {}", id);
        return assignmentMapper.selectById(id);
    }

    @Transactional
    public void save(Assignment assignment) {
        logger.debug("保存作业, id: {}, title: {}, teacherId: {}", 
            assignment.getId(), assignment.getTitle(), assignment.getTeacherId());
        if (assignment.getId() == null) {
            assignmentMapper.insert(assignment);
        } else {
            assignmentMapper.update(assignment);
        }
    }

    @Transactional
    public void delete(Long id) {
        logger.debug("删除作业, id: {}", id);
        assignmentMapper.delete(id);
    }
}
