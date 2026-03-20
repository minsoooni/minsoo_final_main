package com.spring.app.admin.domain;

import lombok.Data;
import java.time.LocalDate;

@Data
public class AdminPostDTO {
    private Long postId;
    private String memberId;
    private Long boardId;
    private String title;
    private String content;
    private String PostStatus;
    private int viewCount;
    private LocalDate createdAt;
    private LocalDate updatedAt;
    private int isHidden;
}