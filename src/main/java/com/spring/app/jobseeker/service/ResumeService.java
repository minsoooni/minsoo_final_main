package com.spring.app.jobseeker.service;

import java.util.List;

import com.spring.app.jobseeker.domain.*;

public interface ResumeService {

    // 이력서 등록 (기본 + 하위 항목)
    int insertResume(ResumeDTO dto);

    // 이력서 수정 (하위 항목 전체삭제 후 재등록)
    int updateResume(ResumeDTO dto);

    // 이력서 삭제 (소프트 삭제)
    int deleteResume(long resumeId, String memberid);

    // 이력서 1건 상세 조회 (기본 + 하위 항목 전체)
    ResumeDTO selectResumeOne(long resumeId);

    // 특정 회원의 이력서 목록 조회
    List<ResumeDTO> selectResumeListByMember(String memberid);

    // 특정 회원의 이력서 개수 조회
    int selectResumeCountByMember(String memberid);

    // 이력서 작성용 회원 프로필 조회
    java.util.Map<String, Object> selectMemberProfile(String memberid);

    // 대표 이력서 설정
    int setPrimaryResume(long resumeId, String memberid, int allowScout);

    // 제안 허용 설정
    int updateAllowScout(long resumeId, String memberid, int allowScout);

    // 자격증 마스터 검색 (자동완성)
    List<CertificateDTO> searchCertificateMaster(String keyword);

    // 직무 카테고리 목록
    List<JobCategoryDTO> selectJobCategoryList();

    // 지역 목록
    List<RegionDTO> selectRegionList();

    // 기술스택 검색 (자동완성)
    List<ResumeTechstackDTO> searchSkill(String keyword);
}
