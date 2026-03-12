package com.spring.app.admin.model;

import com.spring.app.admin.domain.AdminNoticeDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface AdminNoticeDAO {

    // 공지사항 목록 조회 (페이징 + 검색)
    List<AdminNoticeDTO> selectPagedNotices(
            @Param("search") String search,
            @Param("offset") int offset,
            @Param("limit")  int limit
    );

    // 전체 건수 (페이지바 계산용)
    int selectNoticeCount(@Param("search") String search);

    // 중요 공지 건수 (통계 카드용)
    int selectNoticeCountByPinned(@Param("pinnedYn") String pinnedYn);

    // 게시중 건수 (통계 카드용)
    int selectNoticeCountByStatus(@Param("status") int status);

    // 팝업 공지 건수 (통계 카드용)
    int selectPopupCount();

    // 단건 조회 (수정 모달 / 팝업 미리보기용)
    AdminNoticeDTO selectNoticeById(@Param("noticeId") Long noticeId);

    // 공지사항 등록
    int insertNotice(AdminNoticeDTO dto);

    // 공지사항 수정
    int updateNotice(AdminNoticeDTO dto);

    // 공지사항 삭제
    int deleteNotice(@Param("noticeId") Long noticeId);

    // 중요/일반 토글 (Y↔N)
    int togglePinnedYn(@Param("noticeId") Long noticeId);

    // 팝업 활성화 (기존 팝업 해제 + 새 팝업 ON 동시 처리)
    int activatePopup(@Param("noticeId") Long noticeId);

    // 팝업 해제 (단건)
    int deactivatePopup(@Param("noticeId") Long noticeId);
    
    List<AdminNoticeDTO> selectPublicNotices(@Param("search") String search,
            @Param("offset") int offset,
            @Param("limit") int limit);
	
    int selectPublicNoticeCount(@Param("search") String search);
	int increaseViewCount(Long noticeId);
	
	AdminNoticeDTO selectActivePopup();

	int expireNotices();
}