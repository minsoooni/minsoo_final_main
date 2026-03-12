package com.spring.app.jobseeker.service;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.spring.app.jobseeker.domain.ImageFileDTO;
import com.spring.app.jobseeker.domain.JobApplicationDTO;
import com.spring.app.jobseeker.domain.SubmittedResumeDTO;

public interface ApplyService {

    boolean hasAlreadyApplied(long jobId, String memberid);

    long submitApplication(long jobId, long resumeId, String memberid, List<MultipartFile> files) throws Exception;

    List<JobApplicationDTO> getApplicationList(String memberid);

    JobApplicationDTO getApplicationDetail(long applicationId, String memberid);

    List<ImageFileDTO> getApplicationFiles(long applicationId);

    int cancelApplication(long applicationId, String memberid);

    Map<String, Integer> getApplicationStatusCounts(String memberid);

    SubmittedResumeDTO getSubmittedResume(long submittedResumeId);

    // 기술스택/자격증 상세 목록
    List<Map<String, Object>> getApplicationTechstackList(long submittedResumeId);
    List<Map<String, Object>> getApplicationCertificateList(long submittedResumeId);

    // 지원 진행상태 이력 조회
    List<Map<String, Object>> getApplicationHistory(long applicationId);
}
