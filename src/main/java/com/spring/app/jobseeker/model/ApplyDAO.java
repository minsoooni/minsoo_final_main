package com.spring.app.jobseeker.model;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.spring.app.jobseeker.domain.ImageFileDTO;
import com.spring.app.jobseeker.domain.JobApplicationDTO;
import com.spring.app.jobseeker.domain.SubmittedResumeDTO;

@Mapper
public interface ApplyDAO {

    // === 지원 전 검증 === //
    int selectApplicationCount(@Param("jobId") long jobId, @Param("memberid") String memberid);
    int selectMaxApplicationRound(@Param("jobId") long jobId, @Param("memberid") String memberid);

    // === 지원서 스냅샷 (tbl_submitted_resume) === //
    int insertSubmittedResume(SubmittedResumeDTO dto);
    SubmittedResumeDTO selectSubmittedResume(@Param("submittedResumeId") long submittedResumeId);

    // === 지원서 기술스택 / 자격증 === //
    int insertApplicationTechstack(@Param("submittedResumeId") long submittedResumeId,
                                   @Param("skillId") long skillId,
                                   @Param("proficiency") int proficiency);

    int insertApplicationCertificate(@Param("submittedResumeId") long submittedResumeId,
                                     @Param("certificateCode") String certificateCode,
                                     @Param("acquiredDate") String acquiredDate);

    // 기술스택 상세 목록 조회 (skill_name + proficiency)
    List<Map<String, Object>> selectApplicationTechstackList(@Param("submittedResumeId") long submittedResumeId);

    // 자격증 상세 목록 조회 (certificate_name + issuer + acquired_date)
    List<Map<String, Object>> selectApplicationCertificateList(@Param("submittedResumeId") long submittedResumeId);

    // === 입사지원 (tbl_job_application) === //
    int insertJobApplication(JobApplicationDTO dto);

    // === 첨부파일 (tbl_image_file) === //
    int insertImageFile(ImageFileDTO dto);
    List<ImageFileDTO> selectImageFileList(@Param("targetId") long targetId, @Param("targetType") String targetType);

    // === 지원 내역 조회 === //
    List<JobApplicationDTO> selectApplicationList(@Param("memberid") String memberid);
    JobApplicationDTO selectApplicationDetail(@Param("applicationId") long applicationId, @Param("memberid") String memberid);
    int cancelApplication(@Param("applicationId") long applicationId, @Param("memberid") String memberid);
    List<Map<String, Object>> selectApplicationStatusCounts(@Param("memberid") String memberid);

    // 지원 진행상태 이력 조회
    List<Map<String, Object>> selectApplicationHistory(@Param("applicationId") long applicationId);

    // 알림용 공고 정보 조회 (공고 제목 + 기업 회원 ID)
    Map<String, Object> selectJobPostingForNoti(@Param("jobId") long jobId);
}
