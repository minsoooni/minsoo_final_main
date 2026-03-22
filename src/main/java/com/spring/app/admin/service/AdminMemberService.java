package com.spring.app.admin.service;

import com.spring.app.admin.domain.AdminMemberDTO;
import java.util.List;

public interface AdminMemberService {

    // 일반 회원 목록 조회 (검색어, 상태 필터 적용)
    List<AdminMemberDTO> getMemberList(String search, String status);

    // 회원 정지 (STATUS → 1)
    int suspendMember(String memberId);

    // 회원 정지 해제 (STATUS → 0)
    int unsuspendMember(String memberId);

    // 10개 단위 페이징 기법
	List<AdminMemberDTO> getPagedMemberList(String search, String status, int page, int limit);
	
	// 회원 수 구하기 (페이지바 계산)
	int getMemberCount(String search, String status);
	
	AdminMemberDTO getMemberById(String memberId);

	int withdrawMember(String memberId);
	
}