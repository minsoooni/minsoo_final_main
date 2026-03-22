package com.spring.app.admin.model;
import com.spring.app.admin.domain.AdminMemberDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface AdminMemberDAO {
    // 일반 회원 목록 조회
    List<AdminMemberDTO> selectMemberList(
            @Param("search") String search,
            @Param("status") String status
    );
    // 회원 상태 변경 (정지/정지해제)
    int updateMemberStatus(
            @Param("memberId") String memberId,
            @Param("status")   String status
    );
    // 10개 단위 페이징 기법
    List<AdminMemberDTO> selectPagedMemberList(
            @Param("search") String search,
            @Param("status") String status,
            @Param("offset") int offset,
            @Param("endRow") int endRow,  // ← 추가
            @Param("limit")  int limit
    );
    // 회원 수 구하기 (페이지바 계산)
    int selectMemberCount(
            @Param("search") String search,
            @Param("status") String status
    );

    AdminMemberDTO selectMemberById(@Param("memberId") String memberId);
    int withdrawMember(@Param("memberId") String memberId);
}