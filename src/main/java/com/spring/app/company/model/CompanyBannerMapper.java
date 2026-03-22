package com.spring.app.company.model;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.spring.app.company.domain.BannerDTO;
import com.spring.app.company.domain.BannerListDTO;
import com.spring.app.company.domain.ImageFileDTO;
import com.spring.app.company.domain.JobPostingDTO;

@Mapper
//채용공고 매퍼파일
public interface CompanyBannerMapper {

	// 공고 목록 조회
    List<JobPostingDTO> getBannerPostingList(@Param("memberId") String memberId);

    // 배너 시퀀스
    Long getBannerSeq();

    // 배너 등록
    int insertBanner(BannerDTO bannerDto);

    // 이미지 파일 시퀀스
    Long getImageFileSeq();

    // 이미지 파일 등록
    int insertImageFile(ImageFileDTO imageDto);

    // 배너에 이미지 파일 번호 업데이트
    int updateBannerImageFileId(@Param("bannerId") Long bannerId,
                                @Param("fileId") Long fileId);

    
    // 배너리스트 조회
    //List<BannerListDTO> selectBannerListByMemberId(@Param("memberId") String memberId);
    
    
    // 배너 전체 개수 조회
    int getBannerCountByMemberId(@Param("memberId") String memberId);

    // 배너 리스트 페이징 조회
    List<BannerListDTO> selectBannerListByMemberIdPaging(Map<String, Object> paraMap);
	
	
    // 배너 종료일이 지난 경우 상태를 '마감'으로 변경
    int updateBannerStatusToClosed();
    
    
    // 배너 삭제(소프트 삭제)
    int deleteBannerByBannerId(@Param("bannerId") Long bannerId,
                               @Param("memberId") String memberId);
    
}
