package com.spring.app.company.model;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.spring.app.company.domain.ApplicantDetailDTO;
import com.spring.app.company.domain.ApplicantListDTO;
import com.spring.app.company.domain.ImageFileDTO;

@Mapper
//지원자 매퍼파일
public interface CompanyApplicantMapper {

	// 지원자 목록 조회
    List<ApplicantListDTO> selectApplicantList(Map<String, Object> paraMap);

    // 지원자 상세를 클릭했을 때 읽었음으로 상태 변경
    int updateApplicantReadStatus(Map<String, Object> paraMap);
    
    // 현재 상태 조회
    Integer selectApplicantCurrentStatus(Map<String, Object> paraMap);

    // 상태 변경
    int updateApplicantStatus(Map<String, Object> paraMap);

    // 변경 이력 저장
    int insertApplicationHistory(Map<String, Object> paraMap);

    // 구직자 이력서 상세 조회
    ApplicantDetailDTO selectApplicantDetailForCompany(Map<String, Object> paraMap);


    List<ImageFileDTO> getApplicationFiles(Long applicationId);

    List<Map<String, Object>> getApplicationTechstackList(Long submittedResumeId);

    List<Map<String, Object>> getApplicationCertificateList(Long submittedResumeId);


}
