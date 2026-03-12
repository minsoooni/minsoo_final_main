package com.spring.app.admin.service;

import java.util.Map;

public interface AdminStatisticsservice {
    Map<String, Object> getChartData(String from, String to, String period);
}
