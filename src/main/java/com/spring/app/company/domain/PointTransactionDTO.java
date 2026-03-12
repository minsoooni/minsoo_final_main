package com.spring.app.company.domain;

import lombok.Data;

@Data
//포인트 거래이력 DTO
public class PointTransactionDTO {
	private Long pointTxId;        // 포인트거래ID
    private Long pointWalletId;    // 포인트지갑ID
    private Long paymentId;        // 결제ID (nullable 가능)
    private Long bannerId;         // 배너ID (nullable 가능)

    private String txType;         // 거래유형 (CHARGE/USE/REFUND/RESERVE/RELEASE 등)
    private String txStatus;       // 거래상태 (PENDING/SUCCESS/FAILED/CANCELED 등)

    private Long deltaAvailable;   // 사용가능포인트 변동량 (+/-)
    private Long deltaReserved;    // 예약포인트 변동량 (+/-)

    private Long availableBalanceAfter;   // 거래 후 사용가능 잔액
    private Long reservedBalanceAfter;    // 거래 후 reserved 잔액

    private String txLabel;               // 충전 / 사용 / 환불
    private String txMemo;                // 포인트 충전 / 배너 광고 결제 등

    private String createdAt;        // 등록일시
}
