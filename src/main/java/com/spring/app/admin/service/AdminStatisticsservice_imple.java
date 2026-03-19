package com.spring.app.admin.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.spring.app.admin.model.AdminStatisticsDAO;

import java.util.*;

@Service
@RequiredArgsConstructor
public class AdminStatisticsservice_imple implements AdminStatisticsservice {

    private final AdminStatisticsDAO adminStatisticsDAO;

    @Override
    public Map<String, Object> getChartData(String from, String to, String period) {
        Map<String, Object> result = new HashMap<>();

        result.put("joinTrend",     toChart(adminStatisticsDAO.selectMemberJoinTrend(from, to, period)));
        result.put("withdrawTrend", toChart(adminStatisticsDAO.selectMemberWithdrawTrend(from, to, period)));

        if ("monthly".equals(period)) {
            result.put("dayChart",      toMonthChart(adminStatisticsDAO.selectJoinByMonth(from, to)));
            result.put("dayChartTitle", "월별 가입 건수");
        } else {
            result.put("dayChart",      toDayOfWeekChart(adminStatisticsDAO.selectJoinByDayOfWeek(from, to)));
            result.put("dayChartTitle", "요일별 평균 가입 수");
        }

        result.put("memberGrowth", toChart(adminStatisticsDAO.selectMemberGrowthLast12()));
        result.put("jobReg",       toChart(adminStatisticsDAO.selectJobRegTrend(from, to, period)));
        result.put("jobClosed",    toChart(adminStatisticsDAO.selectJobClosedTrend(from, to, period)));
        result.put("postTrend",    toChart(adminStatisticsDAO.selectPostTrend(from, to, period)));
        result.put("commentTrend", toChart(adminStatisticsDAO.selectCommentTrend(from, to, period)));

        return result;
    }

    /** DT + CNT 구조 → { labels, data } */
    private Map<String, Object> toChart(List<Map<String, Object>> rows) {
        List<String> labels = new ArrayList<>();
        List<Long>   data   = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            labels.add(String.valueOf(row.get("DT")));
            Object cnt = row.get("CNT");
            data.add(cnt == null ? 0L : ((Number) cnt).longValue());
        }
        return Map.of("labels", labels, "data", data);
    }

    /** 요일별: Oracle D = 1(일)~7(토) → 월화수목금토일 순 */
    private Map<String, Object> toDayOfWeekChart(List<Map<String, Object>> rows) {
        long[] counts = new long[7];
        for (Map<String, Object> row : rows) {
            int dayNum = ((Number) row.get("DAY_NUM")).intValue();
            counts[dayNum - 1] = ((Number) row.get("CNT")).longValue();
        }
        String[] orderedNames  = {"월","화","수","목","금","토","일"};
        long[]   orderedCounts = {counts[1],counts[2],counts[3],counts[4],counts[5],counts[6],counts[0]};
        List<Long> data = new ArrayList<>();
        for (long c : orderedCounts) data.add(c);
        return Map.of("labels", Arrays.asList(orderedNames), "data", data);
    }

    /** 월별 가입: 1~12월 전체 */
    private Map<String, Object> toMonthChart(List<Map<String, Object>> rows) {
        long[] counts = new long[12];
        for (Map<String, Object> row : rows) {
            int m = ((Number) row.get("MONTH_NUM")).intValue();
            counts[m - 1] = ((Number) row.get("CNT")).longValue();
        }
        String[] months = {"1월","2월","3월","4월","5월","6월","7월","8월","9월","10월","11월","12월"};
        List<Long> data = new ArrayList<>();
        for (long c : counts) data.add(c);
        return Map.of("labels", Arrays.asList(months), "data", data);
    }
}
