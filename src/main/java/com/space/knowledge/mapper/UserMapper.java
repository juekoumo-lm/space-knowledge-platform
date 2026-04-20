package com.space.knowledge.mapper;

import com.space.knowledge.entity.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface UserMapper {
    User selectById(Long id);
    User selectByUsername(String username);
    List<User> selectAll();
    List<User> selectByRole(@Param("roleId") Integer roleId);
    List<User> selectByGrade(@Param("gradeId") Integer gradeId);
    List<User> selectByClass(@Param("classId") Integer classId);
    int insert(User user);
    int update(User user);
    int updateLastLogin(@Param("id") Long id, @Param("lastLogin") java.time.LocalDateTime lastLogin);
    int updatePassword(@Param("id") Long id, @Param("passwordHash") String passwordHash);
    int delete(Long id);
}
