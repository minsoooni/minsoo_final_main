package com.spring.app.admin.model;

import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AdminStatisticsDAO {

    List<Map<String, Object>> selectMemberJoinTrend(
        @Param("from") String from, @Param("to") String to, @Param("period") String period);

    List<Map<String, Object>> selectMemberWithdrawTrend(
        @Param("from") String from, @Param("to") String to, @Param("period") String period);

    List<Map<String, Object>> selectJoinByDayOfWeek(
        @Param("from") String from, @Param("to") String to);

    List<Map<String, Object>> selectJoinByMonth(
        @Param("from") String from, @Param("to") String to);

    List<Map<String, Object>> selectMemberGrowthLast12();

    List<Map<String, Object>> selectJobRegTrend(
        @Param("from") String from, @Param("to") String to, @Param("period") String period);

    List<Map<String, Object>> selectJobClosedTrend(
        @Param("from") String from, @Param("to") String to, @Param("period") String period);

    List<Map<String, Object>> selectPostTrend(
        @Param("from") String from, @Param("to") String to, @Param("period") String period);

    List<Map<String, Object>> selectCommentTrend(
        @Param("from") String from, @Param("to") String to, @Param("period") String period);
}
