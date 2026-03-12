package com.spring.app.admin.model;

import com.spring.app.admin.domain.AdminBannerDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface AdminBannerDAO {

	// 배너 목록 조회 (페이징)
	List<AdminBannerDTO> selectBannerList(@Param("offset") int offset, @Param("limit") int limit);

	// 전체 건수
	int selectBannerCount();

    // 승인
    int updateBannerApprove(@Param("bannerId") Long bannerId);

    // 거절 (사유 포함)
    int updateBannerReject(
            @Param("bannerId") Long bannerId,
            @Param("rejectReason") String rejectReason
    );

    // 정지
    int updateBannerStopped(@Param("bannerId") Long bannerId);

    // 재승인
    int updateBannerUnstopped(@Param("bannerId") Long bannerId);

    // 수정
    int selectBannerCountByStatus(@Param("status") String status);// 페이징 기법

    AdminBannerDTO selectBannerById(@Param("bannerId") Long bannerId);

    // 승인된 배너
	List<AdminBannerDTO> selectActiveBannerList();
}