package com.space.knowledge.mapper;

import com.space.knowledge.entity.Attempt;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface AttemptMapper {
    int insert(Attempt attempt);
    List<Attempt> selectByUserId(@Param("userId") Long userId, @Param("limit") int limit);
    List<Attempt> selectRecentByUserId(@Param("userId") Long userId, @Param("n") int n);
    int countCorrectByUser(@Param("userId") Long userId, @Param("since") LocalDateTime since);
    int countByUserSince(@Param("userId") Long userId, @Param("since") LocalDateTime since);
}
