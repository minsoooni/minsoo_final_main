package com.spring.app.admin.service;

import com.spring.app.admin.domain.AdminCommentDTO;
import com.spring.app.admin.domain.AdminPostDTO;
import com.spring.app.admin.model.AdminPostDAO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminPostService_imple implements AdminPostService {

    private final AdminPostDAO adminPostDAO;

    @Override
    public List<AdminPostDTO> getPagedPosts(String search, Integer isHidden, int page, int limit) {
        int offset = (page - 1) * limit;
        return adminPostDAO.selectPagedPosts(search, isHidden, offset, limit);
    }

    @Override
    public int getPostCount(String search, Integer isHidden) {
        return adminPostDAO.selectPostCount(search, isHidden);
    }

    @Override
    public int getPostCountByHidden(int isHidden) {
        return adminPostDAO.selectPostCountByHidden(isHidden);
    }

    @Override
    public int updatePostHidden(Long postId, int isHidden) {
        return adminPostDAO.updatePostHidden(postId, isHidden);
    }

    @Override
    public List<AdminCommentDTO> getPagedComments(String search, Integer isHidden, int page, int limit) {
        int offset = (page - 1) * limit;
        return adminPostDAO.selectPagedComments(search, isHidden, offset, limit);
    }

    @Override
    public int getCommentCount(String search, Integer isHidden) {
        return adminPostDAO.selectCommentCount(search, isHidden);
    }

    @Override
    public int getCommentCountByHidden(int isHidden) {
        return adminPostDAO.selectCommentCountByHidden(isHidden);
    }

    @Override
    public int updateCommentHidden(Long commentId, int isHidden) {
        return adminPostDAO.updateCommentHidden(commentId, isHidden);
    }
}