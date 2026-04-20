package com.space.knowledge.mapper;

import com.space.knowledge.entity.Badge;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BadgeMapper {
    List<Badge> selectAll();
    Badge selectById(Integer id);
    void insert(Badge badge);
    void update(Badge badge);
    void delete(Integer id);
    List<Integer> selectBadgeIdsByUserId(Long userId);
    void insertUserBadge(@Param("userId") Long userId, @Param("badgeId") Integer badgeId);
    void deleteUserBadge(@Param("userId") Long userId, @Param("badgeId") Integer badgeId);
}
