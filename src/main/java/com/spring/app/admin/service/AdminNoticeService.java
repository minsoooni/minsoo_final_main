package com.spring.app.admin.service;

import com.spring.app.admin.domain.AdminNoticeDTO;
import java.util.List;

public interface AdminNoticeService {
    // 목록 조회 (페이징 + 검색)
    List<AdminNoticeDTO> getPagedNotices(String search, int page, int limit);
    // 전체 건수
    int getNoticeCount(String search);
    // 중요 공지 건수
    int getNoticeCountByPinned(String pinnedYn);
    // 게시중 건수
    int getNoticeCountByStatus(int status);
    // 팝업 건수
    int getPopupCount();
    // 단건 조회
    AdminNoticeDTO getNoticeById(Long noticeId);
    // 등록
    int createNotice(AdminNoticeDTO dto);
    // 수정
    int updateNotice(AdminNoticeDTO dto);
    // 삭제
    int deleteNotice(Long noticeId);
    // 중요/일반 토글
    int togglePinnedYn(Long noticeId);
    // 팝업 활성화
    int activatePopup(Long noticeId);
    // 팝업 해제 (단건)
    int deactivatePopup(Long noticeId);
    
    List<AdminNoticeDTO> getPublicNotices(String search, int page, int limit);
    int getPublicNoticeCount(String search);
    AdminNoticeDTO getNoticeDetail(Long noticeId); // 조회수 증가 포함
    
    AdminNoticeDTO getActivePopup();
}