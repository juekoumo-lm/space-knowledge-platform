package com.space.knowledge.controller;

import com.space.knowledge.common.Result;
import com.space.knowledge.mapper.StatisticsMapper;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Collections;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    @Resource
    private StatisticsMapper statisticsMapper;

    /** 关卡通过率（按年级） */
    @GetMapping("/level-pass-rate")
    public Result<List<Map<String, Object>>> levelPassRate(@RequestParam Integer gradeId) {
        return Result.ok(statisticsMapper.levelPassRateByGrade(gradeId));
    }

    /** 学生活跃度排行 */
    @GetMapping("/student-rank")
    public Result<List<Map<String, Object>>> studentRank(@RequestParam(required = false) Integer classId, @RequestParam(defaultValue = "20") int limit) {
        return Result.ok(statisticsMapper.studentActiveRank(classId, limit));
    }

    /** 高频错题 */
    @GetMapping("/wrong-hot")
    public Result<List<Map<String, Object>>> wrongHot(@RequestParam(required = false) Integer gradeId, @RequestParam(defaultValue = "20") int limit) {
        return Result.ok(statisticsMapper.wrongQuestionHot(gradeId, limit));
    }

    /** 知识点掌握热力图数据 */
    @GetMapping("/kp-mastery")
    public Result<List<Map<String, Object>>> kpMastery(@RequestParam(required = false) Integer classId) {
        return Result.ok(statisticsMapper.kpMasteryHeatmap(classId));
    }

    /** 大屏聚合 */
    @GetMapping("/overview")
    public Result<Map<String, Object>> overview(@RequestParam(required = false) Integer gradeId, @RequestParam(required = false) Integer classId) {
        Map<String, Object> data = new HashMap<>();
        data.put("levelPassRate", gradeId != null ? statisticsMapper.levelPassRateByGrade(gradeId) : Collections.emptyList());
        data.put("studentRank", statisticsMapper.studentActiveRank(classId, 20));
        data.put("wrongHot", statisticsMapper.wrongQuestionHot(gradeId, 15));
        data.put("kpMastery", statisticsMapper.kpMasteryHeatmap(classId));
        return Result.ok(data);
    }
}
