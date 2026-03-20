package com.spring.app.admin.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.spring.app.admin.domain.AdminBannerDTO;
import com.spring.app.admin.model.AdminBannerDAO;
import com.spring.app.notification.domain.NotificationDTO;
import com.spring.app.notification.model.NotificationDAO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminBannerService_imple implements AdminBannerService {

    private final AdminBannerDAO bannerAdminDao;
    private final NotificationDAO notificationDAO;

    @Override
    public List<AdminBannerDTO> getBannerList(int page, int limit, String status) {
        int offset = (page - 1) * limit;
        return bannerAdminDao.selectBannerList(offset, limit, status);
    }

    @Override
    public int getBannerCount(String status) {
        return bannerAdminDao.selectBannerCount(status);
    }
    
    @Override
    public int getBannerTotalCount() {
        return bannerAdminDao.selectBannerTotalCount();
    }

    @Override
    public int getBannerCountByStatus(String status) {
        return bannerAdminDao.selectBannerCountByStatus(status);
    }

    @Override
    @Transactional
    public int approveBanner(Long bannerId) {
        int result = bannerAdminDao.updateBannerApprove(bannerId);

        if (result > 0) {
            AdminBannerDTO banner = bannerAdminDao.selectBannerById(bannerId);
            NotificationDTO noti = new NotificationDTO();
            noti.setFkMemberId(banner.getFkMemberId());
            noti.setType("BANNER");
            noti.setTitle("배너 신청 승인 안내");
            noti.setMessage("신청하신 배너 [" + banner.getTitle() + "] 이(가) 승인되었습니다.");
            noti.setLinkUrl("/company/banner/list");
            notificationDAO.insertNotification(noti);
        }

        return result;
    }

    @Override
    @Transactional
    public int rejectBanner(Long bannerId, String reason) {
        int result = bannerAdminDao.updateBannerReject(bannerId, reason);

        if (result > 0) {
            AdminBannerDTO banner = bannerAdminDao.selectBannerById(bannerId);

            // 환불 처리
            Long useAmount = bannerAdminDao.selectBannerUseAmount(bannerId);
            if (useAmount != null && useAmount > 0) {
                Long walletId = bannerAdminDao.selectWalletIdByMemberId(banner.getFkMemberId());
                if (walletId != null) {
                    // 환불 트랜잭션 기록
                    bannerAdminDao.insertRefundTransaction(walletId, bannerId, useAmount);
                    // 지갑 잔액 복구
                    bannerAdminDao.updateWalletRefund(walletId, useAmount);
                }
            }

            // 알림 발송
            NotificationDTO noti = new NotificationDTO();
            noti.setFkMemberId(banner.getFkMemberId());
            noti.setType("BANNER");
            noti.setTitle("배너 신청 거절 안내");
            noti.setMessage("신청하신 배너 [" + banner.getTitle() + "] 이(가) 거절되었습니다.\n사유: " + reason);
            noti.setLinkUrl("/company/banner/list");
            notificationDAO.insertNotification(noti);
        }

        return result;
    }

    @Override
    @Transactional
    public int stoppedBanner(Long bannerId) {
        int result = bannerAdminDao.updateBannerStopped(bannerId);

        if (result > 0) {
            AdminBannerDTO banner = bannerAdminDao.selectBannerById(bannerId);
            NotificationDTO noti = new NotificationDTO();
            noti.setFkMemberId(banner.getFkMemberId());
            noti.setType("BANNER");
            noti.setTitle("배너 정지 안내");
            noti.setMessage("배너 [" + banner.getTitle() + "] 이(가) 정지되었습니다.");
            noti.setLinkUrl("/company/banner/list");
            notificationDAO.insertNotification(noti);
        }

        return result;
    }

    @Override
    @Transactional
    public int unstoppedBanner(Long bannerId) {
        int result = bannerAdminDao.updateBannerUnstopped(bannerId);
        if (result > 0) {
            AdminBannerDTO banner = bannerAdminDao.selectBannerById(bannerId);
            NotificationDTO noti = new NotificationDTO();
            noti.setFkMemberId(banner.getFkMemberId());
            noti.setType("BANNER");
            noti.setTitle("배너 정지 해제 안내");
            noti.setMessage("배너 [" + banner.getTitle() + "] 이(가) 정지 해제되어 다시 게재됩니다.");
            noti.setLinkUrl("/company/banner/list");
            notificationDAO.insertNotification(noti);
        }
        return result;
    }

    @Override
    public List<AdminBannerDTO> getActiveBanners() {
        return bannerAdminDao.selectActiveBannerList();
    }
}
