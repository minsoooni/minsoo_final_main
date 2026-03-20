package com.spring.app.admin.model;

import com.spring.app.admin.domain.AdminBannerDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface AdminBannerDAO {
    // 배너 목록 조회 (페이징)
	List<AdminBannerDTO> selectBannerList(@Param("offset") int offset, @Param("limit") int limit, @Param("status") String status);
    // 전체 건수
	int selectBannerCount(@Param("status") String status);
	// 전체 개수
	int selectBannerTotalCount();
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
    // 상태별 건수
    int selectBannerCountByStatus(@Param("status") String status);
    // 단건 조회
    AdminBannerDTO selectBannerById(@Param("bannerId") Long bannerId);
    // 승인된 배너
    List<AdminBannerDTO> selectActiveBannerList();

    // 환불용 - 배너의 USE 트랜잭션 금액 조회
    Long selectBannerUseAmount(@Param("bannerId") Long bannerId);

    // 환불용 - 환불 트랜잭션 INSERT
    int insertRefundTransaction(
            @Param("walletId") Long walletId,
            @Param("bannerId") Long bannerId,
            @Param("amount") Long amount
    );

    // 환불용 - 지갑 잔액 UPDATE
    int updateWalletRefund(
            @Param("walletId") Long walletId,
            @Param("amount") Long amount
    );

    // 환불용 - 지갑 ID 조회 (fkMemberId로)
    Long selectWalletIdByMemberId(@Param("fkMemberId") String fkMemberId);

    
}
