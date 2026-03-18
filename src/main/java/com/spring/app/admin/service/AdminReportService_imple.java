package com.spring.app.admin.service;

import com.spring.app.admin.domain.AdminReportDTO;
import com.spring.app.admin.model.AdminReportDAO;
import com.spring.app.notification.domain.NotificationDTO;
import com.spring.app.notification.model.NotificationDAO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminReportService_imple implements AdminReportService {

    private final AdminReportDAO adminReportDAO;
    private final NotificationDAO notificationDAO;

    @Override
    public List<AdminReportDTO> getPagedReports(String search, String type, String reason, String status, int page, int limit) {
        int offset = (page - 1) * limit;
        return adminReportDAO.selectPagedReports(search, type, reason, status, offset, limit);
    }

    @Override
    public int getReportCount(String search, String type, String reason, String status) {
        return adminReportDAO.selectReportCount(search, type, reason, status);
    }

    @Override
    public int getReportCountByType(int type) {
        return adminReportDAO.selectReportCountByType(type);
    }

    @Override
    @Transactional
    public int updateProcessStatus(Long reportId, String status, String reason) {

        // 한글 그대로 DB 저장 ("승인" / "반려")
        int result = adminReportDAO.updateProcessStatus(reportId, status, reason);

        if (result > 0) {
            AdminReportDTO report = adminReportDAO.selectReportById(reportId);

            if ("승인".equals(status)) {

                Long targetId = report.getTargetId();

                switch (report.getTargetType()) {
                    case 1: // 채용공고
                        adminReportDAO.updateJobHidden(targetId);
                        break;

                    case 2: // 게시글
                        adminReportDAO.updatePostHidden(targetId);
                        break;

                    case 3: // 댓글
                        adminReportDAO.updateCommentHidden(targetId);
                        break;
                }
            }           
            
            String targetTitle;
            String linkUrl;

            if (report.getTargetType() == 1) {
                targetTitle = "채용공고";
                linkUrl = "/user-service/job/detail/" + report.getTargetId(); // user-service
            } else if (report.getTargetType() == 2) {
                targetTitle = "게시글";
                linkUrl = "/board-service/community/view?postId=" + report.getTargetId(); // board-service ✅
            } else {
                targetTitle = "댓글";
                linkUrl = "/board-service/community/view?postId=" + report.getCommentPostId(); // board-service ✅
            }

            NotificationDTO noti = new NotificationDTO();
            noti.setType("REPORT");
            noti.setLinkUrl(linkUrl);

            if ("승인".equals(status)) {
                // 승인 → 신고 당한 사람에게 알림
                noti.setFkMemberId(report.getReportedMemberId());
                noti.setTitle("콘텐츠 신고 처리 안내");
                noti.setMessage("회원님의 " + targetTitle + " [" + report.getTargetTitle() + "] 이(가) 신고 처리되었습니다.\n사유: " + reason);
            } else {
                // 반려 → 신고한 사람에게 알림
                noti.setFkMemberId(report.getReporterMemberId());
                noti.setTitle("신고 반려 안내");
                noti.setMessage("접수하신 " + targetTitle + " [" + report.getTargetTitle() + "] 신고가 반려되었습니다.\n사유: " + reason);
            }

            notificationDAO.insertNotification(noti);
        }

        return result;
    }
}