package com.spring.app.admin.service;

import com.spring.app.admin.domain.AdminCommentDTO;
import com.spring.app.admin.domain.AdminPostDTO;
import java.util.List;

public interface AdminPostService {

    // 게시글
    List<AdminPostDTO> getPagedPosts(String search, Integer isHidden, int page, int limit);
    int getPostCount(String search, Integer isHidden);

    int getActivePostCount();     
    int getHiddenPostCount();     
    int getDeletedPostCount();

    int updatePostHidden(Long postId, int isHidden);

    // 댓글
    List<AdminCommentDTO> getPagedComments(String search, Integer isHidden, int page, int limit);
    int getCommentCount(String search, Integer isHidden);

    int getActiveCommentCount();  
    int getHiddenCommentCount();  
    int getDeletedCommentCount();

    int updateCommentHidden(Long commentId, int isHidden);
    List<AdminPostDTO> getDeletedPagedPosts(String search, int page, int limit);
    List<AdminCommentDTO> getDeletedPagedComments(String search, int page, int limit);
}