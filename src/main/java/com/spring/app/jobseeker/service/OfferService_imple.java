package com.spring.app.jobseeker.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.spring.app.jobseeker.domain.OfferReceivedDTO;
import com.spring.app.jobseeker.model.OfferDAO;

@Service
public class OfferService_imple implements OfferService {

    private final OfferDAO offerDAO;

    public OfferService_imple(OfferDAO offerDAO) {
        this.offerDAO = offerDAO;
    }

    // 구직자가 받은 제안 전체 목록 조회
    @Override
    public List<OfferReceivedDTO> getReceivedOffers(String memberId) {
        return offerDAO.selectReceivedOffers(memberId);
    }

    // 상태별 건수 조회
    @Override
    public Map<String, Object> getOfferCounts(String memberId) {
        return offerDAO.selectOfferCounts(memberId);
    }

    // 제안 열람 처리
    @Transactional
    @Override
    public int markAsViewed(long offerSubmitId, String memberId) {
        return offerDAO.updateViewedAt(offerSubmitId, memberId);
    }

    // 제안 수락
    @Transactional
    @Override
    public int acceptOffer(long offerSubmitId, String memberId) {
        return offerDAO.updateAccept(offerSubmitId, memberId);
    }

    // 제안 거절
    @Transactional
    @Override
    public int rejectOffer(long offerSubmitId, String memberId) {
        return offerDAO.updateReject(offerSubmitId, memberId);
    }
}
