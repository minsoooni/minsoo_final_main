package com.spring.app.company.service;

import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import com.spring.app.common.domain.EducationDTO;
import com.spring.app.common.domain.JobCategoryDTO;
import com.spring.app.common.domain.RegionDTO;
import com.spring.app.common.domain.SkillCategoryDTO;
import com.spring.app.common.domain.SkillDTO;
import com.spring.app.company.domain.ApplicantDetailDTO;
import com.spring.app.company.domain.ApplicantListDTO;
import com.spring.app.company.domain.BannerDTO;
import com.spring.app.company.domain.BannerListDTO;
import com.spring.app.company.domain.CompanyDashboardDTO;
import com.spring.app.company.domain.CompanyProfileDTO;
import com.spring.app.company.domain.CompanyProfileUpdateDTO;
import com.spring.app.company.domain.CompanyProfileUpdateResponseDTO;
import com.spring.app.company.domain.CompanyTopbarDTO;
import com.spring.app.company.domain.DeletedOfferHistoryDTO;
import com.spring.app.company.domain.ImageFileDTO;
import com.spring.app.company.domain.JobPostingDTO;
import com.spring.app.company.domain.JobPostingEditResponseDTO;
import com.spring.app.company.domain.MemberSimpleDTO;
import com.spring.app.company.domain.OfferCreateRequestDTO;
import com.spring.app.company.domain.OfferDetailDTO;
import com.spring.app.company.domain.OfferListDTO;
import com.spring.app.company.domain.OfferMetricsDTO;
import com.spring.app.company.domain.OfferMetricsSummaryDTO;
import com.spring.app.company.domain.OfferRecipientDetailDTO;
import com.spring.app.company.domain.OfferSendRequestDTO;
import com.spring.app.company.domain.OfferUpdateRequestDTO;
import com.spring.app.company.domain.PaymentCompleteRequest;
import com.spring.app.company.domain.PaymentCompleteResponse;
import com.spring.app.company.domain.PaymentReadyRequest;
import com.spring.app.company.domain.PaymentReadyResponse;
import com.spring.app.company.domain.TalentResumeDTO;
import com.spring.app.company.domain.TalentResumeDetailDTO;
import com.spring.app.company.domain.TalentSearchConditionDTO;

public interface CompanyService {

	//기업 상단바 조회(기업ID, 기업명, 이메일)
	CompanyTopbarDTO getCompanyTopbarInfo(String memberId);
	
	
	// 기업 대시보드 전체 조회
    CompanyDashboardDTO getCompanyDashboard(String memberId);
    
    // 기업 프로필 조회
    CompanyProfileDTO getCompanyProfile(String memberId);
    
    // 기업 기본 정보 수정
    CompanyProfileUpdateResponseDTO updateBasicProfile(CompanyProfileUpdateDTO dto);

    // 기업 주소 정보 수정
    CompanyProfileUpdateResponseDTO updateAddressProfile(CompanyProfileUpdateDTO dto);

    // 기업 소개 정보 수정
    CompanyProfileUpdateResponseDTO updateIntroProfile(CompanyProfileUpdateDTO dto);
    
    //기업 로고 등록
    CompanyProfileUpdateResponseDTO uploadCompanyLogo(String memberId, MultipartFile logoFile) throws Exception;
    
    
	
	//채용공고 리스트 조회하기(다른 메서드에서 사용함)
	List<JobPostingDTO> getJobPostingList(String memberId);
	
	//채용공고 리스트 조회하기(페이징처리)
	List<JobPostingDTO> getJobPostingListPaing(Map<String, Object> paraMap);
	int getJobPostingCount(String memberId); //공고 전체갯수
	
	//채용공고 삭제하기
	//int deleteJobPosting(Long jobId);
	int deleteJobPosting(Long jobId, String memberId);
	
	//선택된 공고 상세정보 조회하기
	JobPostingDTO getJobPostingOne(Long jobId);
	
	//기존 채용공고 데이터 조회하기
	JobPostingEditResponseDTO getJobPostingForEdit(Long jobId);
	
	//채용공고 수정하기
	int updateJobPosting(JobPostingDTO dto, List<Long> skillIds);
	
	
	//직무 및 기술스택 매핑테이블에 트랜잭션 처리하여 채용공고 등록하기
	int insertJobPosting(JobPostingDTO dto, List<Long> skillIds);
	
	//상태 동기화(마감/게시일 종료에 따른 변경)
	void refreshJobPostingStatuses();

	
	//학력 리스트 조회해오기
	List<EducationDTO> selectEduList();
	
	//직무 카테고리 조회해오기
	List<JobCategoryDTO> getRoots(); //대분류
	List<JobCategoryDTO> getChildren(Long parentId); //중분류
	
	//스킬 카테고리 조회해오기
	List<SkillCategoryDTO> getSkillCategoryWithSkills();
	
	//지역 카테고리 조회해오기
	List<RegionDTO> getRegionLevel1();
    List<RegionDTO> getRegionChildren(String parentCode);
    
    
    
    
    
    
    // 공고 필터용
    //List<JobPostingDTO> selectJobPostingListByMemberId(String memberId);

    // 지원자 목록 조회
    //List<ApplicantListDTO> selectApplicantList(Map<String, Object> paraMap);
    
    // 지원자 목록 총 개수
    int selectApplicantCount(Map<String, Object> paraMap);
    // 지원자 목록 페이징 조회
    List<ApplicantListDTO> selectApplicantListPaging(Map<String, Object> paraMap);
    
    // 상세 보기 클릭했을 때 읽었음으로 변경
    boolean readApplicantDetail(Map<String, Object> paraMap);

    // 지원자 상태 변경 + 이력 저장
    boolean updateApplicantStatus(Map<String, Object> paraMap);
    
    // 지원자 이력서 상세 조회
    ApplicantDetailDTO getApplicantDetailForCompany(Map<String, Object> paraMap);

    // 지원자 이력서 첨부파일
    List<ImageFileDTO> getApplicationFiles(Long applicationId);
    // 지원자 이력서 기술스택
    List<Map<String, Object>> getApplicationTechstackList(Long submittedResumeId);
    // 지원자 이력서 자격증
    List<Map<String, Object>> getApplicationCertificateList(Long submittedResumeId);
    
    
    
    
    
    
    
    
    
    //발송 대상 구직자 목록 조회하기
    List<MemberSimpleDTO> getReceiverList();
    
    //발송한 제안서 전체 통계 조회하기
    OfferMetricsSummaryDTO selectOfferMetricsSummary(String companyMemberId);
    //제안서_응답을 통해 발송한 제안서 상태 조회하기
    List<OfferMetricsDTO> selectOfferMetricsByCompany(String companyMemberId);
    
    //제안서 리스트 조회해오기
  	List<OfferListDTO> selectOfferList(String companyMemberId);
  	//제안서 상세정보 조회해오기
  	OfferDetailDTO selectOfferDetail(Long id);
  	
  	//제안서 등록하기
  	Long createOfferLetter(OfferCreateRequestDTO req);
  	
  	//제안서 수정하기
	int updateOfferLetter(OfferUpdateRequestDTO req);

	//제안서 작제하기
	int deleteOfferLetter(long offerLetterId, String companyMemberId);
	
	//제안서 발송하기
	Long sendOffer(OfferSendRequestDTO req, String companyMemberId);

	//제안서를 발송한 회원(memberId) 목록 조회
	List<String> selectSentMemberIdsByOfferLetterId(Long offerLetterId, String companyMemberId);
	
	//제안서 수신자 상세 조회
	List<OfferRecipientDetailDTO> selectOfferRecipientDetailsByOfferLetterId(Long offerLetterId, String companyMemberId);
	
	// 삭제된 원본 제안서 중 발송 이력이 있는 목록
	List<DeletedOfferHistoryDTO> selectDeletedOfferHistoryList(String companyMemberId);
	
	
	
	
	//결제 준비
    PaymentReadyResponse preparePointCharge(PaymentReadyRequest req, Authentication authentication);
    //결제 완료(서버 검증 + 포인트 적립)
    PaymentCompleteResponse completePointCharge(PaymentCompleteRequest req);
    // 결제 재확인(서버 오류/응답 끊김 이후 복구용)
    PaymentCompleteResponse reconcilePointCharge(String orderId);
    // 결제창 취소 시 PENDING 상태 주문을 CANCELED로 변경
    PaymentCompleteResponse cancelPendingPayment(String orderId);

    //지갑 페이지 데이터
    //Map<String, Object> getWalletPageData(String memberId, String tab);
    Map<String, Object> getWalletPageData(String memberId, String tab, int currentShowPageNo, int sizePerPage);

    
    
    
    
    //배너 등록
	void insertBannerWithImage(BannerDTO bannerDto, MultipartFile bannerImage) throws Exception;

	//해당 기업의 공고 목록 조회하기
	List<JobPostingDTO> getBannerPostingList(String memberId);
	
	//배너 리스트 조회하기
	//List<BannerListDTO> getBannerListByMemberId(String memberId);
	
	//배너 갯수 조회하기
	int getBannerCountByMemberId(String memberId);
	//페이징처리를 위한 배너 리스트 조회하기
	List<BannerListDTO> getBannerListByMemberIdPaging(Map<String, Object> paraMap);
	
	//배너 등록 화면용 포인트 정보 조회 메서드
	Map<String, Object> getBannerPaymentInfo(String memberId);
	
	// 배너 종료일 기준 상태 동기화(마감 처리)
	void refreshBannerStatuses();
	
	// 마감된 배너 삭제 처리
	boolean deleteBanner(Long bannerId, String memberId);
	
	
	
	//===필터 데이터===//
	// 직무분야 목록
    List<JobCategoryDTO> getJobCategoryList();
    // 기술 카테고리 목록
    List<SkillCategoryDTO> getSkillCategoryList();
    // 기술 목록
    List<SkillDTO> getSkillList();
    
    // 공개 대표이력서 목록
    List<TalentResumeDTO> getPublicPrimaryResumeList(TalentSearchConditionDTO searchDto);

    // 공개 대표이력서 수
    int getPublicPrimaryResumeCount(TalentSearchConditionDTO searchDto);

    // 공개 대표이력서 상세
    TalentResumeDetailDTO getPublicPrimaryResumeDetail(Long resumeId);
    
    
}
