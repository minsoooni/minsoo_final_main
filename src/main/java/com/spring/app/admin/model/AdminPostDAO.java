package com.spring.app.admin.model;

import com.spring.app.admin.domain.AdminCommentDTO;
import com.spring.app.admin.domain.AdminPostDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface AdminPostDAO {

    // 게시글
    List<AdminPostDTO> selectPagedPosts(
        @Param("search") String search,
        @Param("isHidden") Integer isHidden,
        @Param("offset") int offset,
        @Param("limit") int limit
    );
    int selectPostCount(
        @Param("search") String search,
        @Param("isHidden") Integer isHidden
    );
    int selectPostCountByHidden(@Param("isHidden") int isHidden);
    int updatePostHidden(@Param("postId") Long postId, @Param("isHidden") int isHidden);

    // 댓글
    List<AdminCommentDTO> selectPagedComments(
        @Param("search") String search,
        @Param("isHidden") Integer isHidden,
        @Param("offset") int offset,
        @Param("limit") int limit
    );
    int selectCommentCount(
        @Param("search") String search,
        @Param("isHidden") Integer isHidden
    );
    int selectCommentCountByHidden(@Param("isHidden") int isHidden);
    int updateCommentHidden(@Param("commentId") Long commentId, @Param("isHidden") int isHidden);
	int selectDeletedPostCount();
	int selectDeletedCommentCount();
	
	List<AdminPostDTO> selectDeletedPagedPosts(
		    @Param("search") String search,
		    @Param("offset") int offset,
		    @Param("limit") int limit
		);
	List<AdminCommentDTO> selectDeletedPagedComments(
		    @Param("search") String search,
		    @Param("offset") int offset,
		    @Param("limit")  int limit
		);
}