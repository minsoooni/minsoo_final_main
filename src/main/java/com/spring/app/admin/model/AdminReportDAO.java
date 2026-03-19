package com.spring.app.admin.model;

import com.spring.app.admin.domain.AdminReportDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface AdminReportDAO {

    List<AdminReportDTO> selectPagedReports(
        @Param("search") String search,
        @Param("type")   String type,
        @Param("reason") String reason,
        @Param("status") String status,
        @Param("offset") int offset,
        @Param("limit")  int limit
    );

    int selectReportCount(
        @Param("search") String search,
        @Param("type")   String type,
        @Param("reason") String reason,
        @Param("status") String status
    );

    int selectReportCountByType(@Param("type") int type);

    int updateProcessStatus(
        @Param("reportId") Long reportId,
        @Param("status") String status,
        @Param("reason") String reason
    );

    int updatePostHidden(@Param("postId") Long targetId);
    int updateCommentHidden(@Param("commentId") Long targetId);
    int updateJobHidden(@Param("jobId") Long targetId);

    AdminReportDTO selectReportById(@Param("reportId") Long reportId);
}