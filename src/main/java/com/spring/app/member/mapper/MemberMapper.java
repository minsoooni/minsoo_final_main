package com.spring.app.member.mapper;

import java.time.LocalDateTime;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.spring.app.member.domain.CompanyMemberDTO;
import com.spring.app.member.domain.MemberDTO;

@Mapper
public interface MemberMapper {

    // ================================ 공용 ================================

    // 회원 아이디 중복 여부 확인
    int countByMemberId(@Param("memberId") String memberId);

    //이메일 중복 여부 확인
    int countByEmail(@Param("email") String email);

    // 회원 가입 시 권한 정보 저장
    int insertAuthority(@Param("memberId") String memberId,
                        @Param("authority") String authority);

    // SMS 전송을 위한 휴대폰 조회 
    String selectPhoneByMemberId(@Param("memberId") String memberId);

    // 권한 목록 조회 (로그인 성공 시 Security 권한 세팅용)
    List<String> selectAuthorities(@Param("memberId") String memberId);

    // memberId로 회원 정보 조회
    MemberDTO selectByMemberId(@Param("memberId") String memberId);

    // 로그인 실패횟수 확인
    Integer selectFailCount(@Param("memberId") String memberId);

    // 로그인 실패횟수 증가 및 마지막 실패 일자 기입
    int upsertIncreaseFail(@Param("memberId") String memberId,
                           @Param("now") LocalDateTime now);

    // 실패 횟수 초기화 
    int resetFail(@Param("memberId") String memberId);

    // 로그인 메타(STATUS, TYPE, LAST_LOGIN_AT) 조회
    MemberDTO selectLoginMeta(@Param("memberId") String memberId);

    // 휴면 전환 처리
    int markDormant(@Param("memberId") String memberId,
                    @Param("now") LocalDateTime now);

    // 마지막 로그인일 갱신
    int updateLastLoginAt(@Param("memberId") String memberId,
                          @Param("now") LocalDateTime now);

    // 휴면 해제 처리 (STATUS=1, DORMANT_AT=NULL, LAST_LOGIN_AT=now)
    int unlockDormant(@Param("memberId") String memberId,
                      @Param("now") LocalDateTime now);

    // 비밀번호 변경 의무 여부 수정
    int updateMustChangePasswordYn(@Param("memberId") String memberId, @Param("mustChangePasswordYn") int mustChangePasswordYn);

    // 비밀번호 변경 처리
    int updatePassword(@Param("memberId") String memberId,@Param("encodedPassword") String encodedPassword);


    // ================================ 개인회원 ================================

    //구직자 회원 정보 등록
    int insertMember(MemberDTO dto);

    //구직자 로그인 시 회원 정보 조회
    MemberDTO selectJobSeekerForLogin(@Param("memberId") String memberId);

    // 구직자 회원 아이디 찾기
    String findMemberId(MemberDTO dto);

    // 구직자 회원 비밀번호 찾기용 회원 조회
    MemberDTO findMemberForPassword(MemberDTO dto);


    // ================================ 기업회원 ================================

    //사업자번호 중복 여부 확인
    int countCompanyByBizNo(@Param("bizNo") String bizNo);

    // 기업 회원 및 기업 정보 등록
    int insertCompany(CompanyMemberDTO dto);

    //기업 로그인 시 회원 정보 조회
    MemberDTO selectCompanyForLogin(@Param("memberId") String memberId);

    // 기업 승인 여부 조회 (1=승인, 0=미승인)
    Integer selectCompanyApprovedYn(@Param("memberId") String memberId);

    // 반려 사유 조회 (기업용)
    String selectCompanyRejectReason(@Param("memberId") String memberId);

    // 기업 회원 아이디 찾기
    String findCompanyId(CompanyMemberDTO dto);

    // 기업 회원 비밀번호 찾기용 회원 조회
    CompanyMemberDTO findCompanyForPassword(CompanyMemberDTO dto);
    
    // Refresh Token 저장(있으면 수정, 없으면 등록)
    int upsertRefreshToken(@Param("memberId") String memberId,
                           @Param("refreshToken") String refreshToken);

    // Refresh Token 조회
    String selectRefreshToken(@Param("memberId") String memberId);

    // Refresh Token 삭제
    int deleteRefreshToken(@Param("memberId") String memberId);
    
    
}