package com.spring.app.admin.domain;

import java.util.Date;

import lombok.Data;

@Data
public class AdminNoticeDTO {
    private Long   noticeId;
    private String title;
    private String content;
    private int    viewCount;
    private String pinnedYn;   // Y/N
    private String popupYn;    // Y/N
    private Date   popupStartAt;
    private Date   popupEndAt;
    private int    status;     // 1=게시중, 0=마감
    private Date   createdAt;
    private Date   updatedAt;
    private Date   endDate;
    
}
