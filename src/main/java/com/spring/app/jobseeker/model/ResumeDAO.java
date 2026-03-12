package com.spring.app.jobseeker.model;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.spring.app.jobseeker.domain.*;

@Mapper
public interface ResumeDAO {

    // === 이력서 기본 CRUD === //
    int insertResume(ResumeDTO dto);
    int updateResume(ResumeDTO dto);
    int deleteResume(@Param("resumeId") long resumeId, @Param("memberid") String memberid);
    ResumeDTO selectResumeOne(long resumeId);
    List<ResumeDTO> selectResumeListByMember(String memberid);
    int clearPrimaryResume(String memberid);
    int setPrimaryResume(@Param("resumeId") long resumeId, @Param("memberid") String memberid);
    int updateAllowScout(@Param("resumeId") long resumeId, @Param("memberid") String memberid, @Param("allowScout") int allowScout);

    // === 이력서 개수 조회 === //
    int selectResumeCountByMember(String memberid);

    // === 이력서 작성용 회원 프로필 조회 === //
    java.util.Map<String, Object> selectMemberProfile(String memberid);

    // === 학력 === //
    int insertEducation(ResumeEducationDTO dto);
    int deleteEducationByResumeId(long resumeId);
    List<ResumeEducationDTO> selectEducationList(long resumeId);

    // === 경력 === //
    int insertCareer(ResumeCareerDTO dto);
    int deleteCareerByResumeId(long resumeId);
    List<ResumeCareerDTO> selectCareerList(long resumeId);

    // === 어학 === //
    int insertLanguage(ResumeLanguageDTO dto);
    int deleteLanguageByResumeId(long resumeId);
    List<ResumeLanguageDTO> selectLanguageList(long resumeId);

    // === 자격증 === //
    int insertCertificate(ResumeCertificateDTO dto);
    int deleteCertificateByResumeId(long resumeId);
    List<ResumeCertificateDTO> selectCertificateList(long resumeId);

    // === 포트폴리오 === //
    int insertPortfolio(ResumePortfolioDTO dto);
    int deletePortfolioByResumeId(long resumeId);
    List<ResumePortfolioDTO> selectPortfolioList(long resumeId);

    // === 수상 === //
    int insertAward(ResumeAwardDTO dto);
    int deleteAwardByResumeId(long resumeId);
    List<ResumeAwardDTO> selectAwardList(long resumeId);

    // === 기술스택 === //
    int insertTechstack(ResumeTechstackDTO dto);
    int deleteTechstackByResumeId(long resumeId);
    List<ResumeTechstackDTO> selectTechstackList(long resumeId);

    // === 자격증 검색 (자동완성) === //
    List<CertificateDTO> searchCertificateMaster(String keyword);

    // === 직무 카테고리 목록 === //
    List<JobCategoryDTO> selectJobCategoryList();

    // === 지역 목록 === //
    List<RegionDTO> selectRegionList();

    // === 기술스택 검색 (자동완성) === //
    List<ResumeTechstackDTO> searchSkill(String keyword);
}
