package com.spring.app.admin.service;

import com.spring.app.admin.domain.AdminNoticeDTO;
import com.spring.app.admin.model.AdminNoticeDAO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminNoticeService_imple implements AdminNoticeService {

    private final AdminNoticeDAO noticeAdminDao;

    @Override
    public List<AdminNoticeDTO> getPagedNotices(String search, int page, int limit) {
        
    	int expiredCount = noticeAdminDao.expireNotices();
    	
    	int offset = (page - 1) * limit;
        return noticeAdminDao.selectPagedNotices(search, offset, limit);
    }
    
    @Override
    public int getNoticeCount(String search) {
        return noticeAdminDao.selectNoticeCount(search);
    }

    @Override
    public int getNoticeCountByPinned(String pinnedYn) {
        return noticeAdminDao.selectNoticeCountByPinned(pinnedYn);
    }

    @Override
    public int getNoticeCountByStatus(int status) {
        return noticeAdminDao.selectNoticeCountByStatus(status);
    }

    @Override
    public int getPopupCount() {
        return noticeAdminDao.selectPopupCount();
    }

    @Override
    public AdminNoticeDTO getNoticeById(Long noticeId) {
        return noticeAdminDao.selectNoticeById(noticeId);
    }

    @Override
    @Transactional
    public int createNotice(AdminNoticeDTO dto) {
    	 dto.setStatus(1);
        int result = noticeAdminDao.insertNotice(dto);
        // 팝업 ON이면 다른 팝업 전부 해제 후 이 공지만 활성화
        if ("Y".equals(dto.getPopupYn())) {
            noticeAdminDao.activatePopup(dto.getNoticeId());
        }
        return result;
    }

    @Override
    @Transactional
    public int updateNotice(AdminNoticeDTO dto) {
        int result = noticeAdminDao.updateNotice(dto);
        // 팝업 ON이면 다른 팝업 전부 해제 후 이 공지만 활성화
        if ("Y".equals(dto.getPopupYn())) {
            noticeAdminDao.activatePopup(dto.getNoticeId());
        }
        return result;
    }

    @Override
    @Transactional
    public int deleteNotice(Long noticeId) {
        return noticeAdminDao.deleteNotice(noticeId);
    }

    @Override
    @Transactional
    public int togglePinnedYn(Long noticeId) {
        return noticeAdminDao.togglePinnedYn(noticeId);
    }
    
    
     

    @Override
    @Transactional
    public int activatePopup(Long noticeId) {
        return noticeAdminDao.activatePopup(noticeId);
    }

    @Override
    @Transactional
    public int deactivatePopup(Long noticeId) {
        return noticeAdminDao.deactivatePopup(noticeId);
    }
    
    @Override
    public List<AdminNoticeDTO> getPublicNotices(String search, int page, int limit) {
        
    	
    	int expiredCount = noticeAdminDao.expireNotices();
    	int offset = (page - 1) * limit;
        return noticeAdminDao.selectPublicNotices(search, offset, limit);
    }

    @Override
    public int getPublicNoticeCount(String search) {
        return noticeAdminDao.selectPublicNoticeCount(search);
    }

    @Override
    @Transactional
    public AdminNoticeDTO getNoticeDetail(Long noticeId) {
        noticeAdminDao.increaseViewCount(noticeId);
        return noticeAdminDao.selectNoticeById(noticeId);
    }
    
    @Override
    public AdminNoticeDTO getActivePopup() {
    	
    	int expiredCount = noticeAdminDao.expireNotices();
    	
        return noticeAdminDao.selectActivePopup();
    }

}