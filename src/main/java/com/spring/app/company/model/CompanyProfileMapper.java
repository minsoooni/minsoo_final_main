package com.spring.app.company.model;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.spring.app.company.domain.CompanyProfileDTO;
import com.spring.app.company.domain.CompanyProfileUpdateDTO;
import com.spring.app.company.domain.CompanyTopbarDTO;
import com.spring.app.company.domain.ImageFileDTO;

@Mapper
//기업 프로필 매퍼파일
public interface CompanyProfileMapper {
	
	//기업 상단바 조회(기업ID, 기업명, 이메일)
	CompanyTopbarDTO selectCompanyTopbarInfo(String memberId);

	
	// 기업 프로필 조회
    CompanyProfileDTO selectCompanyProfile(@Param("memberId") String memberId);
    
    // TBL_COMPANY 기본 정보 수정
    int updateCompanyBasicInfo(CompanyProfileUpdateDTO dto);

    // TBL_COMPANY 주소 정보 수정
    int updateCompanyAddressInfo(CompanyProfileUpdateDTO dto);

    // TBL_COMPANY_INTRO 존재 여부 확인
    int existsCompanyIntro(@Param("memberId") String memberId);

    // TBL_COMPANY_INTRO 기본 정보 수정
    int updateCompanyIntroBasicInfo(CompanyProfileUpdateDTO dto);

    // TBL_COMPANY_INTRO 기본 정보 insert
    int insertCompanyIntroBasicInfo(CompanyProfileUpdateDTO dto);

    // TBL_COMPANY_INTRO 소개 상세 수정
    int updateCompanyIntroDetail(CompanyProfileUpdateDTO dto);

    // TBL_COMPANY_INTRO 소개 상세 insert
    int insertCompanyIntroDetail(CompanyProfileUpdateDTO dto);
    
    
    // memberId로 company_intro_id 조회
    Long selectCompanyIntroIdByMemberId(@Param("memberId") String memberId);

    // 기존 회사 로고 조회
    ImageFileDTO selectCompanyLogo(@Param("companyIntroId") Long companyIntroId);

    // image file 시퀀스 조회
    Long getImageFileSeq();

    // 파일 등록을 위한 Company_Intro seq 값 구하기
    Long getCompanyIntroSeq();
    
    // image file insert
    int insertImageFile(ImageFileDTO imageDto);

    // 기존 로고 update
    int updateCompanyLogoFile(ImageFileDTO imageDto);
    
    // 이미지 로고 선등록용 insert
    int insertEmptyCompanyIntro(CompanyProfileUpdateDTO dto);

}
