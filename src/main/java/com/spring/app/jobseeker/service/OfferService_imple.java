package com.spring.app.jobseeker.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.spring.app.jobseeker.domain.OfferReceivedDTO;
import com.spring.app.jobseeker.model.OfferDAO;
import com.spring.app.notification.domain.NotificationDTO;
import com.spring.app.notification.model.NotificationDAO;

@Service
public class OfferService_imple implements OfferService {

    private final OfferDAO offerDAO;
    private final NotificationDAO notificationDAO;

    public OfferService_imple(OfferDAO offerDAO, NotificationDAO notificationDAO) {
        this.offerDAO = offerDAO;
        this.notificationDAO = notificationDAO;
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
        int n = offerDAO.updateAccept(offerSubmitId, memberId);

        // 수락 성공 시 기업에게 알림 발송
        if (n == 1) {
            try {
                Map<String, Object> info = offerDAO.selectOfferInfoForNoti(offerSubmitId, memberId);
                if (info != null) {
                    NotificationDTO noti = new NotificationDTO();
                    noti.setFkMemberId((String) info.get("companyMemberId"));
                    noti.setType("OFFER");
                    noti.setTitle("제안 수락 안내");
                    noti.setMessage(info.get("memberName") + "님이 [" + info.get("offerTitle") + "] 제안을 수락했습니다");
                    noti.setLinkUrl("/company/offer/list");
                    notificationDAO.insertNotification(noti);
                }
            } catch (Exception e) {
                // 알림 실패해도 수락 자체는 성공 처리
            }
        }

        return n;
    }

    // 제안 거절
    @Transactional
    @Override
    public int rejectOffer(long offerSubmitId, String memberId) {
        int n = offerDAO.updateReject(offerSubmitId, memberId);

        // 거절 성공 시 기업에게 알림 발송
        if (n == 1) {
            try {
                Map<String, Object> info = offerDAO.selectOfferInfoForNoti(offerSubmitId, memberId);
                if (info != null) {
                    NotificationDTO noti = new NotificationDTO();
                    noti.setFkMemberId((String) info.get("companyMemberId"));
                    noti.setType("OFFER");
                    noti.setTitle("제안 거절 안내");
                    noti.setMessage(info.get("memberName") + "님이 [" + info.get("offerTitle") + "] 제안을 거절했습니다");
                    noti.setLinkUrl("/company/offer/list");
                    notificationDAO.insertNotification(noti);
                }
            } catch (Exception e) {
                // 알림 실패해도 거절 자체는 성공 처리
            }
        }

        return n;
    }
}
