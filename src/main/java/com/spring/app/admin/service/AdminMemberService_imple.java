package com.spring.app.admin.service;

import com.spring.app.admin.domain.AdminMemberDTO;
import com.spring.app.admin.model.AdminMemberDAO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminMemberService_imple implements AdminMemberService {

    private final AdminMemberDAO memberAdminDao;

    // 일반 회원 목록 조회 (TYPE = 0, 검색어/상태 필터 적용)
    @Override
    public List<AdminMemberDTO> getMemberList(String search, String status) {
        return memberAdminDao.selectMemberList(search, status);
    }

    // 회원 정지 (STATUS → 1)
    @Override
    @Transactional
    public int suspendMember(String memberId) {
        return memberAdminDao.updateMemberStatus(memberId, "SUSPENDED");
    }

    // 회원 정지 해제 (STATUS → 0)
    @Override
    @Transactional
    public int unsuspendMember(String memberId) {
        return memberAdminDao.updateMemberStatus(memberId, "ACTIVE");
    }

 // 10개 단위 페이징 기법
    @Override
    public List<AdminMemberDTO> getPagedMemberList(String search, String status, int page, int limit) {
        int offset = (page - 1) * limit;
        int endRow = offset + limit;  // ← 추가
        return memberAdminDao.selectPagedMemberList(search, status, offset, endRow, limit);  // ← endRow 추가
    }

    // 회원 수 구하기 (페이지바 계산)
    @Override
    public int getMemberCount(String search, String status) {

        return memberAdminDao.selectMemberCount(search, status);
    }
    
    @Override
    public AdminMemberDTO getMemberById(String memberId) {
        return memberAdminDao.selectMemberById(memberId);
    }

    @Override
    public int withdrawMember(String memberId) {
        return memberAdminDao.withdrawMember(memberId);
    }
}