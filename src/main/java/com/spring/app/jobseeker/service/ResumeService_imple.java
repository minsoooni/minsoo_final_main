package com.spring.app.jobseeker.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.spring.app.jobseeker.domain.*;
import com.spring.app.jobseeker.model.ResumeDAO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ResumeService_imple implements ResumeService {

    private final ResumeDAO dao;

    // 이력서 등록 (기본 + 하위 항목 일괄)
    @Override
    @Transactional
    public int insertResume(ResumeDTO dto) {

    	// 대표이력서 중복 방지
        if (dto.getIsPrimary() == 1) {
            dao.clearPrimaryResume(dto.getMemberid());
        }
    	
        // 이력서 기본 정보 등록
        int result = dao.insertResume(dto);
        long resumeId = dto.getResumeId();

        // 학력
        if (dto.getEducationList() != null) {
            for (ResumeEducationDTO edu : dto.getEducationList()) {
                edu.setResumeId(resumeId);
                dao.insertEducation(edu);
            }
        }

        // 경력
        if (dto.getCareerList() != null) {
            for (ResumeCareerDTO career : dto.getCareerList()) {
                career.setResumeId(resumeId);
                dao.insertCareer(career);
            }
        }

        // 어학
        if (dto.getLanguageList() != null) {
            for (ResumeLanguageDTO lang : dto.getLanguageList()) {
                lang.setResumeId(resumeId);
                dao.insertLanguage(lang);
            }
        }

        // 자격증
        if (dto.getCertificateList() != null) {
            for (ResumeCertificateDTO cert : dto.getCertificateList()) {
                cert.setResumeId(resumeId);
                dao.insertCertificate(cert);
            }
        }

        // 포트폴리오
        if (dto.getPortfolioList() != null) {
            for (ResumePortfolioDTO port : dto.getPortfolioList()) {
                port.setResumeId(resumeId);
                dao.insertPortfolio(port);
            }
        }

        // 수상
        if (dto.getAwardList() != null) {
            for (ResumeAwardDTO award : dto.getAwardList()) {
                award.setResumeId(resumeId);
                dao.insertAward(award);
            }
        }

        // 기술스택
        if (dto.getTechstackList() != null) {
            for (ResumeTechstackDTO tech : dto.getTechstackList()) {
                tech.setResumeId(resumeId);
                dao.insertTechstack(tech);
            }
        }

        return result;
    }

    // 이력서 수정 (하위 항목 전체 삭제 후 재등록)
    @Override
    @Transactional
    public int updateResume(ResumeDTO dto) {

        long resumeId = dto.getResumeId();

        // 1. 하위 항목 전체 삭제
        dao.deleteEducationByResumeId(resumeId);
        dao.deleteCareerByResumeId(resumeId);
        dao.deleteLanguageByResumeId(resumeId);
        dao.deleteCertificateByResumeId(resumeId);
        dao.deletePortfolioByResumeId(resumeId);
        dao.deleteAwardByResumeId(resumeId);
        dao.deleteTechstackByResumeId(resumeId);

        // 대표이력서 중복 방지
        if (dto.getIsPrimary() == 1) {
            dao.clearPrimaryResume(dto.getMemberid());
        }
        
        // 2. 기본 정보 수정
        int result = dao.updateResume(dto);

        // 3. 하위 항목 재등록
        if (dto.getEducationList() != null) {
            for (ResumeEducationDTO edu : dto.getEducationList()) {
                edu.setResumeId(resumeId);
                dao.insertEducation(edu);
            }
        }
        if (dto.getCareerList() != null) {
            for (ResumeCareerDTO career : dto.getCareerList()) {
                career.setResumeId(resumeId);
                dao.insertCareer(career);
            }
        }
        if (dto.getLanguageList() != null) {
            for (ResumeLanguageDTO lang : dto.getLanguageList()) {
                lang.setResumeId(resumeId);
                dao.insertLanguage(lang);
            }
        }
        if (dto.getCertificateList() != null) {
            for (ResumeCertificateDTO cert : dto.getCertificateList()) {
                cert.setResumeId(resumeId);
                dao.insertCertificate(cert);
            }
        }
        if (dto.getPortfolioList() != null) {
            for (ResumePortfolioDTO port : dto.getPortfolioList()) {
                port.setResumeId(resumeId);
                dao.insertPortfolio(port);
            }
        }
        if (dto.getAwardList() != null) {
            for (ResumeAwardDTO award : dto.getAwardList()) {
                award.setResumeId(resumeId);
                dao.insertAward(award);
            }
        }
        if (dto.getTechstackList() != null) {
            for (ResumeTechstackDTO tech : dto.getTechstackList()) {
                tech.setResumeId(resumeId);
                dao.insertTechstack(tech);
            }
        }

        return result;
    }

    // 이력서 삭제 (소프트 삭제)
    @Override
    public int deleteResume(long resumeId, String memberid) {
        return dao.deleteResume(resumeId, memberid);
    }

    // 이력서 1건 상세 조회 (기본 + 하위 항목)
    @Override
    public ResumeDTO selectResumeOne(long resumeId) {
        ResumeDTO resume = dao.selectResumeOne(resumeId);
        if (resume != null) {
            resume.setEducationList(dao.selectEducationList(resumeId));
            resume.setCareerList(dao.selectCareerList(resumeId));
            resume.setLanguageList(dao.selectLanguageList(resumeId));
            resume.setCertificateList(dao.selectCertificateList(resumeId));
            resume.setPortfolioList(dao.selectPortfolioList(resumeId));
            resume.setAwardList(dao.selectAwardList(resumeId));
            resume.setTechstackList(dao.selectTechstackList(resumeId));
        }
        return resume;
    }

    // 특정 회원의 이력서 목록 조회
    @Override
    public List<ResumeDTO> selectResumeListByMember(String memberid) {
        return dao.selectResumeListByMember(memberid);
    }

    // 특정 회원의 이력서 개수 조회
    @Override
    public int selectResumeCountByMember(String memberid) {
        return dao.selectResumeCountByMember(memberid);
    }

    // 이력서 작성용 회원 프로필 조회
    @Override
    public java.util.Map<String, Object> selectMemberProfile(String memberid) {
        return dao.selectMemberProfile(memberid);
    }

    // 대표 이력서 설정
    @Override
    @Transactional
    public int setPrimaryResume(long resumeId, String memberid, int allowScout) {
        dao.clearPrimaryResume(memberid);
        int result = dao.setPrimaryResume(resumeId, memberid);
        if (result == 1 && allowScout == 1) {
            dao.updateAllowScout(resumeId, memberid, 1);
        }
        return result;
    }

    // 제안 허용 설정
    @Override
    public int updateAllowScout(long resumeId, String memberid, int allowScout) {
        return dao.updateAllowScout(resumeId, memberid, allowScout);
    }

    // 자격증 마스터 검색 (자동완성)
    @Override
    public List<CertificateDTO> searchCertificateMaster(String keyword) {
        return dao.searchCertificateMaster(keyword);
    }

    // 직무 카테고리 목록
    @Override
    public List<JobCategoryDTO> selectJobCategoryList() {
        return dao.selectJobCategoryList();
    }

    // 지역 목록
    @Override
    public List<RegionDTO> selectRegionList() {
        return dao.selectRegionList();
    }

    // 기술스택 검색 (자동완성)
    @Override
    public List<ResumeTechstackDTO> searchSkill(String keyword) {
        return dao.searchSkill(keyword);
    }
}
