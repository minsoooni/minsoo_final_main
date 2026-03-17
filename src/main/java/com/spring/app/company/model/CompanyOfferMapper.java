package com.spring.app.company.model;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.spring.app.company.domain.DeletedOfferHistoryDTO;
import com.spring.app.company.domain.MemberSimpleDTO;
import com.spring.app.company.domain.OfferCreateRequestDTO;
import com.spring.app.company.domain.OfferDetailDTO;
import com.spring.app.company.domain.OfferListDTO;
import com.spring.app.company.domain.OfferMetricsDTO;
import com.spring.app.company.domain.OfferMetricsSummaryDTO;
import com.spring.app.company.domain.OfferRecipientDetailDTO;
import com.spring.app.company.domain.OfferSendValidationDTO;
import com.spring.app.company.domain.OfferUpdateRequestDTO;

@Mapper
public interface CompanyOfferMapper {

    //구직자 정보 간단히 조회해오기
    List<MemberSimpleDTO> selectJobSeekerList();
    
    //발송한 제안서 전체 통계 조회하기
    OfferMetricsSummaryDTO selectOfferMetricsSummary(String companyMemberId);
    //발송한 제안서 상태 조회해오기
    List<OfferMetricsDTO> selectOfferMetricsByCompany(String companyMemberId);
    
	//제안서 리스트 조회해오기
	List<OfferListDTO> selectOfferList(String companyMemberId);
	
	//제안서 상세정보 조회해오기
	OfferDetailDTO selectOfferDetail(Long id);
	
	//제안서 등록(offer_letter)
    int insertOfferLetter(OfferCreateRequestDTO req);
    
    
    //제안서 수정하기
	int updateOfferLetter(OfferUpdateRequestDTO req);
	
	// 소유권 확인(제안서 삭제)
	int existsOfferSendHistory(@Param("offerLetterId") long offerLetterId);
	//제안서 삭제하기
	int deleteOfferLetter(@Param("offerLetterId") long offerLetterId);
	
	
	
	// 소유권 확인(제안서 발송)
	int existsOfferLetterOwnedByCompany(@Param("offerLetterId") Long offerLetterId,
	                                    @Param("companyMemberId") String companyMemberId);
	
	// 발송 기록 권한 확인
    int existsOfferHistoryOwnedByCompany(@Param("offerLetterId") Long offerLetterId,
                                         @Param("companyMemberId") String companyMemberId);

    // 발송 검증용: 제안서 + 연결 공고 정보 조회
    OfferSendValidationDTO selectOfferSendValidationInfo(@Param("offerLetterId") Long offerLetterId);

    // 발송 요청된 수신자들이 정상 구직자인지 확인
    int countValidOfferReceivers(@Param("receiverMemberIds") List<String> receiverMemberIds);

    // 요청한 수신자 중 이미 발송한 회원 조회
    List<String> selectAlreadySentReceiverIds(@Param("offerLetterId") Long offerLetterId,
                                              @Param("receiverMemberIds") List<String> receiverMemberIds);


	// 스냅샷: selectKey 결과를 담기 위해 Map 사용
	int insertOfferSubmitSnapshot(Map<String, Object> param);

	// 매핑테이블 삽입
	int insertOfferResponses(@Param("offerSubmitId") Long offerSubmitId,
	                         @Param("receiverMemberIds") List<String> receiverMemberIds);
	
	
	//제안서를 발송한 회원(memberId) 목록 조회
	List<String> selectSentMemberIdsByOfferLetterId(Long offerLetterId);
	
	// 제안서 수신자 상세 조회
	List<OfferRecipientDetailDTO> selectOfferRecipientDetailsByOfferLetterId(Long offerLetterId);
	
	// 삭제된 원본 제안서 중 발송 이력이 있는 목록
	List<DeletedOfferHistoryDTO> selectDeletedOfferHistoryList(String companyMemberId);
	
    
}
