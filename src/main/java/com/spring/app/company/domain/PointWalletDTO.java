package com.spring.app.company.domain;

import lombok.Data;

@Data
//포인트 지갑 DTO
public class PointWalletDTO {
	private Long pointWalletId;      // 포인트ID(지갑ID)
    private String memberId;         // 회원ID

    private Long availableBalance;   // 사용가능 포인트
    private Long reservedBalance;    // 대기(예약) 포인트

    private String createAt;           // 등록일시 (DB 컬럼: create_at)
    private String updatedAt;          // 수정일시
}
