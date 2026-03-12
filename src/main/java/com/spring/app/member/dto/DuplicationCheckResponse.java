package com.spring.app.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "DuplicationCheckResponse", description = "회원 아이디 또는 이메일 중복 체크 결과 응답 객체")
public class DuplicationCheckResponse {

    // 중복 검사를 수행한 필드명 (예: memberId, email)
    @Schema(description = "검사한 필드명", example = "memberId")
    private String field;

    // 클라이언트가 요청으로 전달한 값
    @Schema(description = "요청으로 들어온 값", example = "user01")
    private String value;

    // 사용 가능 여부 (true: 사용 가능, false: 이미 존재함)
    @Schema(description = "사용 가능 여부(true=사용 가능, false=중복)", example = "true")
    private boolean available;

    // 기본 생성자
    public DuplicationCheckResponse() {}

    // 전체 필드를 초기화하는 생성자
    public DuplicationCheckResponse(String field, String value, boolean available) {
        this.field = field;
        this.value = value;
        this.available = available;
    }
    // 검사한 필드명 반환
    public String getField() {
        return field;
    }
    // 검사한 필드명 설정
    public void setField(String field) {
        this.field = field;
    }
    // 요청 값 반환
    public String getValue() {
        return value;
    }
    // 요청 값 설정
    public void setValue(String value) {
        this.value = value;
    }
    // 사용 가능 여부 반환
    public boolean isAvailable() {
        return available;
    }
    // 사용 가능 여부 설정
    public void setAvailable(boolean available) {
        this.available = available;
    }
}