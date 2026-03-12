package com.spring.app.admin.model;

import com.spring.app.admin.domain.AdminCompanyDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface AdminCompanyDAO {

    // 기업 목록 조회
    List<AdminCompanyDTO> selectCompanyList(
            @Param("search") String search,
            @Param("status") String status,
            @Param("approval") String approval
    );

    // 승인 상태 변경 (승인/거절)
    int updateApprovedYn(
            @Param("memberId") String memberId,
            @Param("approvedYn") String approvedYn
    );

    // 승인 상태 변경 + 거절 사유
    int updateApprovedYnWithReason(
            @Param("memberId") String memberId,
            @Param("approvedYn") String approvedYn,
            @Param("rejectReason") String rejectReason
    );

    // 회원 상태 변경 (정지/정지해제)
    int updateMemberStatus(
            @Param("memberId") String memberId,
            @Param("status") String status
    );
    
    // 기업 권한 결정
    int insertAuthority(String memberId);
    int deleteAuthority(String memberId);

    // 10개 단위 페이징 기법
    List<AdminCompanyDTO> selectPagedCompanyList(
            @Param("search") String search,
            @Param("status") String status,
            @Param("approval") String approval,
            @Param("offset") int offset,
            @Param("limit") int limit
    );

    // 총 개수를 세는 것
    int selectCompanyCount(
            @Param("search") String search,
            @Param("status") String status,
            @Param("approval") String approval
    );
    
    AdminCompanyDTO selectCompanyById(String memberId);
    
    int updateCompanyInfo(
            @Param("memberId")     String memberId,
            @Param("bizNo")        String bizNo,
            @Param("ceoName")      String ceoName,
            @Param("industryCode") String industryCode,
            @Param("companyName")  String companyName
    );
}