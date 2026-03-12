package com.spring.app.jobseeker.service;

import java.util.List;
import java.util.Map;

import com.spring.app.jobseeker.domain.OfferReceivedDTO;

public interface OfferService {

    // 구직자가 받은 제안 전체 목록 조회
    List<OfferReceivedDTO> getReceivedOffers(String memberId);

    // 상태별 건수 조회
    Map<String, Object> getOfferCounts(String memberId);

    // 제안 열람 처리
    int markAsViewed(long offerSubmitId, String memberId);

    // 제안 수락
    int acceptOffer(long offerSubmitId, String memberId);

    // 제안 거절
    int rejectOffer(long offerSubmitId, String memberId);
}
