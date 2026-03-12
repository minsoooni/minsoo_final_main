package com.spring.app.jobseeker.domain;

import lombok.Data;

/**
 * tbl_image_file : 공용 이미지/파일 테이블 DTO
 */
@Data
public class ImageFileDTO {

    private long fileId;                // 파일 고유번호 (PK, seq_image_file_id)
    private long targetId;              // 연결 대상 ID (예: application_id)
    private String targetType;          // 대상 유형 (예: "APPLICATION")
    private String filecategory;        // 파일 카테고리 (예: "ATTACH")
    private String fileUrl;             // 파일 저장 경로 (서버 저장명)
    private String createdAt;           // 생성일
    private String originalFilename;    // 원본 파일명
}
