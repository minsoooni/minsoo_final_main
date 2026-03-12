package com.spring.app.company.model;

import java.util.Map;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.spring.app.company.domain.PaymentDTO;
import com.spring.app.company.domain.PointTransactionDTO;

@Mapper
//결제-포인트 매퍼파일
public interface CompanyWalletMapper {

	// ===== payment =====
    int insertPayment(@Param("memberId") String memberId,
                      @Param("orderId") String orderId,
                      @Param("chargeAmount") Long chargeAmount,
                      @Param("status") String status);

    Map<String, Object> selectPaymentByOrderId(@Param("orderId") String orderId);

    int updatePaymentPaid(@Param("orderId") String orderId,
            			  @Param("payMethod") String payMethod,
            			  @Param("pgProvider") String pgProvider,
            			  @Param("embPgProvider") String embPgProvider);

    int updatePaymentStatus(@Param("orderId") String orderId,
                            @Param("status") String status);

    // ===== point_wallet =====
    Long selectPointWalletId(@Param("memberId") String memberId);

    int insertPointWallet(@Param("memberId") String memberId);

    int addPointAvailable(@Param("memberId") String memberId,
                          @Param("amount") Long amount);

    
    
    Long selectPointAvailableBalance(@Param("memberId") String memberId);

    // ===== payment (lists/summary) =====
    Map<String, Object> selectPaymentSummary(@Param("memberId") String memberId);

    List<PaymentDTO> selectPaymentList(@Param("memberId") String memberId);

    // ===== point_transaction (list) =====
    List<PointTransactionDTO> selectPointTxList(@Param("memberId") String memberId);

    // ===== point_transaction =====
    int insertPointTransactionCharge(@Param("pointWalletId") Long pointWalletId,
                                     @Param("orderId") String orderId,
                                     @Param("txType") String txType,
                                     @Param("txStatus") String txStatus,
                                     @Param("deltaAvailable") Long deltaAvailable);
   
    
    Map<String, Object> selectPointWalletByMemberId(String memberId);

    int deductPointAvailable(@Param("memberId") String memberId,
                             @Param("amount") long amount);

    int insertPointTransactionBannerUse(@Param("pointWalletId") long pointWalletId,
                                        @Param("bannerId") long bannerId,
                                        @Param("txType") String txType,
                                        @Param("txStatus") String txStatus,
                                        @Param("deltaAvailable") long deltaAvailable);
    
    
}
