package com.spring.app.jobseeker.service;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.spring.app.jobseeker.domain.ImageFileDTO;
import com.spring.app.jobseeker.domain.JobApplicationDTO;
import com.spring.app.jobseeker.domain.SubmittedResumeDTO;


public interface ApplyService {

    // 중복 지원 여부 확인
    boolean hasAlreadyApplied(long jobId, String memberid);

    
    // 입사지원 제출
    //  이력서 조회 → JSON 스냅샷 생성 → 기술/자격증 복사 → 지원 등록 → 첨부파일 저장
    long submitApplication(long jobId, long resumeId, String memberid, List<MultipartFile> files) throws Exception;

    // 내 지원 내역 목록 조회
    List<JobApplicationDTO> getApplicationList(String memberid);

    // 지원서 상세 조회 
    JobApplicationDTO getApplicationDetail(long applicationId, String memberid);

    // 지원서 첨부파일 목록 조회
    List<ImageFileDTO> getApplicationFiles(long applicationId);
    
    // 입사지원 취소
    int cancelApplication(long applicationId, String memberid);

   
    // 지원 진행상태별 건수 집계
    // 반환 : total, submitted, reviewing, interview, passed, rejected
    Map<String, Integer> getApplicationStatusCounts(String memberid);

    // 제출 이력서 스냅샷 조회
    SubmittedResumeDTO getSubmittedResume(long submittedResumeId);

    // 지원서 기술스택 목록 조회 
    List<Map<String, Object>> getApplicationTechstackList(long submittedResumeId);

    // 지원서 자격증 목록 조회 
    List<Map<String, Object>> getApplicationCertificateList(long submittedResumeId);

    // 지원 진행상태 이력 조회 
    List<Map<String, Object>> getApplicationHistory(long applicationId);
}
