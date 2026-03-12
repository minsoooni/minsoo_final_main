package com.spring.app.company.domain;

import java.util.Date;

import lombok.Data;

@Data
public class ImageFileDTO {
	private Long fileId;             // 파일 ID
    private Long targetId;           // 연결 대상 ID (지원서 ID 등)
    private String targetType;       // 연결 대상 타입
    private String fileCategory;     // 파일 카테고리
    private String fileUrl;          // 저장 경로
    private String originalFilename; // 원본 파일명
    
    private Date createdAt;
}
