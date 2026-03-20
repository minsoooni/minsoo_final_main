package com.spring.app.admin.model;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.spring.app.admin.domain.AdminJobPostDTO;

@Mapper
public interface AdminJobPostDAO {

    // 페이징 + 검색/필터
    List<AdminJobPostDTO> selectPagedJobList(
        @Param("search") String search,
        @Param("status") String status,
        @Param("offset") int offset,
        @Param("limit")  int limit
    );

    // 전체 건수 (검색/필터 적용)
    int selectJobCount(
        @Param("search") String search,
        @Param("status") String status
    );

    // 상태별 전체 건수 (통계 카드용)
    int selectJobCountByStatus(@Param("status") String status);

    // 상태 변경
    int updateJobHidden(
    	    @Param("jobId") Long jobId,
    	    @Param("isHidden") Integer isHidden
    );

	int countJobsExcludeClosedDeleted(String search);   
}