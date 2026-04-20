package com.space.knowledge.mapper;

import com.space.knowledge.entity.UserLevelProgress;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserLevelProgressMapper {
    UserLevelProgress select(@Param("userId") Long userId, @Param("levelId") Integer levelId);
    List<UserLevelProgress> selectByUserId(Long userId);
    int insertOrUpdate(UserLevelProgress progress);
}
