package com.spring.app.company.model;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.spring.app.common.domain.JobCategoryDTO;
import com.spring.app.common.domain.SkillCategoryDTO;
import com.spring.app.common.domain.SkillDTO;
import com.spring.app.company.domain.TalentResumeDTO;
import com.spring.app.company.domain.TalentResumeDetailDTO;
import com.spring.app.company.domain.TalentSearchConditionDTO;
import com.spring.app.jobseeker.domain.ResumeAwardDTO;
import com.spring.app.jobseeker.domain.ResumeCareerDTO;
import com.spring.app.jobseeker.domain.ResumeCertificateDTO;
import com.spring.app.jobseeker.domain.ResumeEducationDTO;
import com.spring.app.jobseeker.domain.ResumeLanguageDTO;
import com.spring.app.jobseeker.domain.ResumePortfolioDTO;
import com.spring.app.jobseeker.domain.ResumeTechstackDTO;

@Mapper
//인재검색 매퍼파일
public interface CompanyTalentMapper {

	// 직무분야 조회
    List<JobCategoryDTO> selectJobCategoryList();
    // 기술 카테고리 조회
    List<SkillCategoryDTO> selectSkillCategoryList();
    // 기술 목록 조회
    List<SkillDTO> selectSkillList();
    
    // 공개 대표이력서 목록
    List<TalentResumeDTO> selectPublicPrimaryResumeList(TalentSearchConditionDTO searchDto);

    // 공개 대표이력서 수
    int selectPublicPrimaryResumeCount(TalentSearchConditionDTO searchDto);

    
    // 공개 대표이력서 상세
    TalentResumeDetailDTO selectPublicPrimaryResumeDetail(Long resumeId);
    
    List<ResumeEducationDTO> selectTalentEducationList(Long resumeId);
    List<ResumeCareerDTO> selectTalentCareerList(Long resumeId);
    List<ResumeLanguageDTO> selectTalentLanguageList(Long resumeId);
    List<ResumeCertificateDTO> selectTalentCertificateList(Long resumeId);
    List<ResumePortfolioDTO> selectTalentPortfolioList(Long resumeId);
    List<ResumeAwardDTO> selectTalentAwardList(Long resumeId);
    List<ResumeTechstackDTO> selectTalentTechstackList(Long resumeId);
    
}
