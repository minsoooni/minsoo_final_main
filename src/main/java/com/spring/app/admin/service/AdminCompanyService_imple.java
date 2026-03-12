package com.spring.app.admin.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.spring.app.admin.domain.AdminCompanyDTO;
import com.spring.app.admin.model.AdminCompanyDAO;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminCompanyService_imple implements AdminCompanyService {

    private final AdminCompanyDAO companyAdminDao;

    // 기업 목록 조회 (검색어, 상태, 승인상태 필터 적용)
    @Override
    public List<AdminCompanyDTO> getCompanyList(String search, String status, String approval) {
        return companyAdminDao.selectCompanyList(search, status, approval);
    }

    // 기업 승인 처리 (APPROVED_YN → 1, 권한 부여)
    @Override
    @Transactional
    public int approveCompany(String memberId) {
    	int result = companyAdminDao.updateApprovedYnWithReason(memberId, "APPROVED", "");
        if (result > 0) {
            // 승인 완료 시 ROLE_COMPANY 권한 부여
            companyAdminDao.insertAuthority(memberId);
        }
        return result;
    }

    // 기업 거절 처리 (APPROVED_YN → 2, 거절 사유 저장, 권한 제거)
    @Override
    @Transactional
    public int rejectCompany(String memberId, String reason) {
        int result = companyAdminDao.updateApprovedYnWithReason(memberId, "REJECTED", reason);
        if (result > 0) {
            // 거절 시 ROLE_COMPANY 권한 제거
            companyAdminDao.deleteAuthority(memberId);
        }
        return result;
    }

    // 기업 정지 처리 (STATUS → 1, 권한 제거)
    @Override
    @Transactional
    public int suspendCompany(String memberId) {
        int result = companyAdminDao.updateMemberStatus(memberId, "SUSPENDED");
        if (result > 0) {
            // 정지 시 ROLE_COMPANY 권한 제거
            companyAdminDao.deleteAuthority(memberId);
        }
        return result;
    }

    // 기업 정지 해제 처리 (STATUS → 0, 권한 재부여)
    @Override
    @Transactional
    public int unsuspendCompany(String memberId) {
        int result = companyAdminDao.updateMemberStatus(memberId, "ACTIVE");
        if (result > 0) {
            // 정지 해제 시 ROLE_COMPANY 권한 재부여
            companyAdminDao.insertAuthority(memberId);
        }
        return result;
    }

    // 10개 단위 페이징 기법
    @Override
    public List<AdminCompanyDTO> getPagedCompanyList(String search, String status, String approval, int page, int limit) {

        int offset = (page - 1) * limit;

        return companyAdminDao.selectPagedCompanyList(search, status, approval, offset, limit);
    }

    // 총 개수를 세는 것
    @Override
    public int getCompanyCount(String search, String status, String approval) {

        return companyAdminDao.selectCompanyCount(search, status, approval);
    }
    
    @Override
    public AdminCompanyDTO getCompanyById(String memberId) {
        return companyAdminDao.selectCompanyById(memberId);
    }
    
 // 기업 정보 수정
    @Override
    @Transactional
    public int updateCompanyInfo(String memberId, String bizNo, String ceoName, String industryCode, String companyName) {
        return companyAdminDao.updateCompanyInfo(memberId, bizNo, ceoName, industryCode, companyName);
    }
    
 // 기업 재승인
    @Override
    @Transactional
    public int reapproveCompany(String memberId, String bizNo, String ceoName, String industryCode, String companyName) {
        int infoResult = companyAdminDao.updateCompanyInfo(memberId, bizNo, ceoName, industryCode, companyName);
        if (infoResult <= 0) return 0;
        companyAdminDao.updateMemberStatus(memberId, "ACTIVE");
        companyAdminDao.updateApprovedYnWithReason(memberId, "APPROVED", "");
        companyAdminDao.deleteAuthority(memberId);
        companyAdminDao.insertAuthority(memberId);
        return 1;
    }
}