package com.spring.app.admin.service;

import com.spring.app.admin.domain.AdminReportDTO;
import java.util.List;

public interface AdminReportService {
    List<AdminReportDTO> getPagedReports(String search, String type, String reason, String status, int page, int limit);
    int getReportCount(String search, String type, String reason, String status);
    int getReportCountByType(int type);
	int updateProcessStatus(Long reportId, String status, String reason);	
}