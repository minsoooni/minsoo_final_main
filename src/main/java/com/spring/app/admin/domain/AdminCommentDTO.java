package com.spring.app.admin.domain;

import lombok.Data;
import java.time.LocalDate;

@Data
public class AdminCommentDTO {
    private Long commentId;
    private Long postId;
    private String memberId;
    private String content;
    private Long parentCommentId;
    private String status;
    private LocalDate createdAt;
    private LocalDate updatedAt;
    private int isHidden;
}

/* */