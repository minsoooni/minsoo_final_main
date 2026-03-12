package com.spring.app.admin.service;

import java.util.List;
import com.spring.app.admin.domain.AdminCompanyDTO;

public interface AdminCompanyService {

    // 기업 목록 조회
    List<AdminCompanyDTO> getCompanyList(String search, String status, String approval);

    // 기업 승인
    int approveCompany(String memberId);

    // 기업 거절 (사유 포함)
    int rejectCompany(String memberId, String reason);

    // 기업 정지
    int suspendCompany(String memberId);

    // 기업 정지 해제
    int unsuspendCompany(String memberId);

    // 10개 단위 페이징 기법
	List<AdminCompanyDTO> getPagedCompanyList(String search, String status, String approval, int page, int limit);

	// 총 개수를 세는 것
	int getCompanyCount(String search, String status, String approval);
    
	AdminCompanyDTO getCompanyById(String memberId);
	
	// 기업 정보 수정
	int updateCompanyInfo(String memberId, String bizNo, String ceoName, String industryCode, String companyName);
	
	// 재승인
	int reapproveCompany(String memberId, String bizNo, String ceoName, String industryCode, String companyName);
}