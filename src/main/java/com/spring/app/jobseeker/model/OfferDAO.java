package com.spring.app.jobseeker.model;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.spring.app.jobseeker.domain.OfferReceivedDTO;

@Mapper
public interface OfferDAO {

    // 구직자가 받은 제안 전체 목록 조회
    List<OfferReceivedDTO> selectReceivedOffers(@Param("memberId") String memberId);

    // 상태별 건수 조회 (전체, 미응답(UNREAD+PENDING), 수락, 거절)
    Map<String, Object> selectOfferCounts(@Param("memberId") String memberId);

    // 제안 열람 처리 (viewed_at 업데이트)
    int updateViewedAt(@Param("offerSubmitId") long offerSubmitId,
                       @Param("memberId") String memberId);

    // 제안 수락 처리
    int updateAccept(@Param("offerSubmitId") long offerSubmitId,
                     @Param("memberId") String memberId);

    // 제안 거절 처리
    int updateReject(@Param("offerSubmitId") long offerSubmitId,
                     @Param("memberId") String memberId);

    // 알림용 제안 정보 조회 (제안서 제목 + 기업 회원 ID + 구직자명)
    Map<String, Object> selectOfferInfoForNoti(@Param("offerSubmitId") long offerSubmitId,
                                               @Param("memberId") String memberId);
}
