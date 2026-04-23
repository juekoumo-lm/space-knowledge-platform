package com.space.knowledge.mapper;

import com.space.knowledge.entity.Attempt;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.List;

public interface AttemptMapper {
    int insert(Attempt attempt);
    List<Attempt> selectByUserId(@Param("userId") Long userId, @Param("limit") int limit);
    List<Attempt> selectRecentByUserId(@Param("userId") Long userId, @Param("n") int n);
    int countCorrectByUser(@Param("userId") Long userId, @Param("since") LocalDateTime since);
    int countByUserSince(@Param("userId") Long userId, @Param("since") LocalDateTime since);
    List<Long> selectDistinctQuestionIdsByUser(@Param("userId") Long userId);
    List<Attempt> selectRecentByUserAndLevel(@Param("userId") Long userId, @Param("levelId") Integer levelId, @Param("n") int n);
    Double avgTimeSpentByGradeAndLevel(@Param("gradeId") Integer gradeId, @Param("levelId") Integer levelId);
    Integer countDistinctQuestionsByUser(@Param("userId") Long userId);
    List<Map<String, Object>> selectNeighborOverlap(@Param("userId") Long userId, @Param("limit") int limit);
    List<Map<String, Object>> selectNeighborCorrectQuestionPairs(@Param("userIds") List<Long> userIds,
                                                                 @Param("excludeQuestionIds") List<Long> excludeQuestionIds,
                                                                 @Param("limit") int limit);
}
