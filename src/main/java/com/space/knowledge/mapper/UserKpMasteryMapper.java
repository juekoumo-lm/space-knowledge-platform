package com.space.knowledge.mapper;

import com.space.knowledge.entity.UserKpMastery;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserKpMasteryMapper {
    UserKpMastery select(@Param("userId") Long userId, @Param("kpId") Integer kpId);
    List<UserKpMastery> selectByUserId(Long userId);
    int insertOrUpdate(UserKpMastery mastery);
}
