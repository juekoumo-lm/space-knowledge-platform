package com.space.knowledge.service;

import com.space.knowledge.entity.Badge;
import com.space.knowledge.mapper.BadgeMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
public class BadgeService {

    private static final Logger logger = LoggerFactory.getLogger(BadgeService.class);

    @Resource
    private BadgeMapper badgeMapper;

    public List<Badge> listAll() {
        logger.debug("获取所有徽章");
        return badgeMapper.selectAll();
    }

    public Badge getById(Integer id) {
        logger.debug("获取徽章详情, id: {}", id);
        return badgeMapper.selectById(id);
    }

    @Transactional
    public void save(Badge badge) {
        logger.debug("保存徽章, id: {}, name: {}, conditionType: {}, conditionValue: {}", 
            badge.getId(), badge.getName(), badge.getConditionType(), badge.getConditionValue());
        if (badge.getId() == null) {
            badgeMapper.insert(badge);
        } else {
            badgeMapper.update(badge);
        }
    }

    @Transactional
    public void delete(Integer id) {
        logger.debug("删除徽章, id: {}", id);
        badgeMapper.delete(id);
    }

    public List<Integer> getBadgeIdsByUserId(Long userId) {
        logger.debug("获取用户已获得的徽章ID, userId: {}", userId);
        return badgeMapper.selectBadgeIdsByUserId(userId);
    }

    @Transactional
    public void grantBadgeToUser(Long userId, Integer badgeId) {
        logger.debug("授予用户徽章, userId: {}, badgeId: {}", userId, badgeId);
        badgeMapper.insertUserBadge(userId, badgeId);
    }

    @Transactional
    public void revokeBadgeFromUser(Long userId, Integer badgeId) {
        logger.debug("撤销用户徽章, userId: {}, badgeId: {}", userId, badgeId);
        badgeMapper.deleteUserBadge(userId, badgeId);
    }
}
