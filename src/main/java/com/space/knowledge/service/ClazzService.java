package com.space.knowledge.service;

import com.space.knowledge.entity.Clazz;
import com.space.knowledge.mapper.ClazzMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
public class ClazzService {

    private static final Logger logger = LoggerFactory.getLogger(ClazzService.class);

    @Resource
    private ClazzMapper clazzMapper;

    public List<Clazz> listByGrade(Integer gradeId) {
        logger.debug("获取年级班级列表, gradeId: {}", gradeId);
        return clazzMapper.selectByGradeId(gradeId);
    }

    public Clazz getById(Integer id) {
        logger.debug("获取班级详情, id: {}", id);
        return clazzMapper.selectById(id);
    }

    @Transactional
    public void save(Clazz clazz) {
        logger.debug("保存班级, id: {}, name: {}", clazz.getId(), clazz.getName());
        if (clazz.getId() == null) {
            clazzMapper.insert(clazz);
        } else {
            clazzMapper.update(clazz);
        }
    }

    @Transactional
    public void delete(Integer id) {
        logger.debug("删除班级, id: {}", id);
        clazzMapper.delete(id);
    }
}
