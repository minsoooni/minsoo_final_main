package com.spring.app.company.service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.spring.app.common.FileManager;
import com.spring.app.common.domain.EducationDTO;
import com.spring.app.common.domain.JobCategoryDTO;
import com.spring.app.common.domain.RegionDTO;
import com.spring.app.common.domain.SkillCategoryDTO;
import com.spring.app.common.domain.SkillDTO;
import com.spring.app.common.domain.SkillJoinRowDTO;
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
import com.spring.app.company.domain.OfferSendValidationDTO;
import com.spring.app.company.domain.OfferUpdateRequestDTO;
import com.spring.app.company.domain.PaymentCompleteRequest;
import com.spring.app.company.domain.PaymentCompleteResponse;
import com.spring.app.company.domain.PaymentReadyRequest;
import com.spring.app.company.domain.PaymentReadyResponse;
import com.spring.app.company.domain.RegionChainDTO;
import com.spring.app.company.domain.TalentResumeDTO;
import com.spring.app.company.domain.TalentResumeDetailDTO;
import com.spring.app.company.domain.TalentSearchConditionDTO;
import com.spring.app.company.model.CompanyApplicantMapper;
import com.spring.app.company.model.CompanyBannerMapper;
import com.spring.app.company.model.CompanyDashBoardMapper;
import com.spring.app.company.model.CompanyJobMapper;
import com.spring.app.company.model.CompanyOfferMapper;
import com.spring.app.company.model.CompanyProfileMapper;
import com.spring.app.company.model.CompanyTalentMapper;
import com.spring.app.company.model.CompanyWalletMapper;
import com.spring.app.company.payment.PortOneV1Client;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CompanyService_imple implements CompanyService {
	private final CompanyJobMapper jobMapper;
	private final CompanyOfferMapper offerMapper;
	private final CompanyBannerMapper bannerMapper;
	private final CompanyApplicantMapper applicantMapper;
	private final CompanyDashBoardMapper boardMapper;
	private final CompanyProfileMapper profileMapper;
	private final CompanyTalentMapper talentMapper;
	
	private final CompanyWalletMapper walletMapper;
	private final PortOneV1Client portOneV1Client;
	
	private static final long BANNER_AD_PRICE = 300000L; // 배너 1건당 차감 포인트
	private final FileManager fileManager;
    
    @Value("${file.images-dir}")
    private String uploadPath;
	
    
	// 주문번호 생성(결제)
	private String generateOrderId() {
	    String time = java.time.LocalDateTime.now()
	            .format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

	    int random = (int)(Math.random() * 9000) + 1000;

	    return "ORD" + time + random;
	}
	
	//타입 변경 메서드
	private LocalDate toLocalDate(Date date) {
	    if (date == null) return null;
	    return date.toInstant()
	               .atZone(ZoneId.systemDefault())
	               .toLocalDate();
	}
	
	
	// 신고 처리 상태를 화면용으로 바꾸는 메서드
	private String convertReportProcessStatusText(String status) {
	    if (status == null || status.isBlank()) {
	        return "신고 접수";
	    }

	    switch (status) {
	        case "WAIT":
	        case "PENDING":
	        case "처리전":
	            return "처리대기";

	        case "DONE":
	        case "COMPLETE":
	        case "처리완료":
	            return "처리완료";

	        case "REJECT":
	        case "반려":
	            return "반려";

	        default:
	            return status;
	    }
	}
	
	
	
	
	
	//기업 상단바 조회(기업ID, 기업명, 이메일)
	@Override
	public CompanyTopbarDTO getCompanyTopbarInfo(String memberId) {
	    return profileMapper.selectCompanyTopbarInfo(memberId);
	}
	
	//기업 대시보드 전체 조회해오기
	@Override
    public CompanyDashboardDTO getCompanyDashboard(String memberId) {

        CompanyDashboardDTO dto = new CompanyDashboardDTO();

        // ===== KPI =====
        dto.setOngoingJobCount(boardMapper.selectOngoingJobCount(memberId)); //게시중인 공고

        // 공고 상태별 갯수
        dto.setJobWaitingCount(boardMapper.selectJobWaitingCount(memberId));
        dto.setJobPostingCount(boardMapper.selectJobPostingCount(memberId));
        dto.setJobClosedCount(boardMapper.selectJobClosedCount(memberId));
        
        dto.setTotalApplicantCount(boardMapper.selectTotalApplicantCount(memberId)); //총 지원자
        
        dto.setUnreadApplicantCount(boardMapper.selectUnreadApplicantCount(memberId)); //미확인 지원자
        
        // 지원자 상태별(미열람/면접요청) 구직자 수
        dto.setApplicantUnreadCount(boardMapper.selectApplicantUnreadCount(memberId)); // 미열람
        dto.setApplicantInterviewRequestCount(boardMapper.selectApplicantInterviewRequestCount(memberId)); // 면접요청
        
        
        dto.setSentOfferCount(boardMapper.selectSentOfferCount(memberId)); //발송한 제안서 갯수
        
        // 제안서 상태별 건수
        dto.setOfferPendingCount(boardMapper.selectOfferPendingCount(memberId));
        dto.setOfferAcceptedCount(boardMapper.selectOfferAcceptedCount(memberId));
        dto.setOfferRejectedCount(boardMapper.selectOfferRejectedCount(memberId));

        
        Long pointBalance = boardMapper.selectPointBalance(memberId);
        dto.setPointBalance(pointBalance != null ? pointBalance : 0L);

        dto.setBannerCount(boardMapper.selectBannerCount(memberId)); //배너 갯수
        
        //배너 상태별 갯수
        dto.setBannerPendingCount(boardMapper.selectBannerPendingCount(memberId));
        dto.setBannerApprovedCount(boardMapper.selectBannerApprovedCount(memberId));
        dto.setBannerRejectedCount(boardMapper.selectBannerRejectedCount(memberId));

        // ===== 최근 목록 =====
        dto.setRecentJobs(boardMapper.selectRecentJobs(memberId));
        dto.setRecentApplicants(boardMapper.selectRecentApplicants(memberId));
        dto.setRecentOffers(boardMapper.selectRecentOffers(memberId));

        // null 방지
        if (dto.getRecentJobs() == null) {
            dto.setRecentJobs(Collections.emptyList());
        }

        if (dto.getRecentApplicants() == null) {
            dto.setRecentApplicants(Collections.emptyList());
        }

        if (dto.getRecentOffers() == null) {
            dto.setRecentOffers(Collections.emptyList());
        }

        return dto;
    }
	
	
	
	
	//========================= [기업 프로필] =========================//
	//기업 정보 조회
	@Override
	public CompanyProfileDTO getCompanyProfile(String memberId) {
	    return profileMapper.selectCompanyProfile(memberId);
	}
	
	//기업 기본정보 수정
	@Override
	@Transactional
	public CompanyProfileUpdateResponseDTO updateBasicProfile(CompanyProfileUpdateDTO dto) {
	    CompanyProfileUpdateResponseDTO res = new CompanyProfileUpdateResponseDTO();
	    try {
	        // 설립연도 문자열 공백 제거
	        String openYear = dto.getOpenYear();

	        if (openYear != null) {
	            openYear = openYear.trim();
	        }

	        // 설립연도 검증 및 DATE 변환
	        if (openYear == null || openYear.isEmpty()) {
	            dto.setOpenDate(null);
	        } 
	        else {
	            // 설립연도는 4자리 숫자만 허용
	            if (!openYear.matches("^\\d{4}$")) {
	                res.setSuccess(false);
	                res.setMessage("설립연도는 4자리 숫자로 입력하세요.");
	                return res;
	            }

	            int year = Integer.parseInt(openYear);
	            int currentYear = java.time.LocalDate.now().getYear();

	            // 설립연도 범위 검증
	            if (year < 1800 || year > currentYear) {
	                res.setSuccess(false);
	                res.setMessage("설립연도는 1800년부터 현재 연도 사이여야 합니다.");
	                return res;
	            }

	            // 연도만 입력받으므로 1월 1일 기준으로 DATE 생성
	            dto.setOpenDate(java.sql.Date.valueOf(year + "-01-01"));
	        }

	        // 회사 기본정보 수정
	        int n1 = profileMapper.updateCompanyBasicInfo(dto);

	        // company_intro 존재 여부 확인
	        int exists = profileMapper.existsCompanyIntro(dto.getMemberId());

	        int n2;
	        if (exists > 0) {
	            // 이미 intro 행이 있으면 update 수행
	            n2 = profileMapper.updateCompanyIntroBasicInfo(dto);
	        } 
	        else {
	            // intro 신규 생성 시 PK 시퀀스 값을 먼저 세팅
	            Long companyIntroId = profileMapper.getCompanyIntroSeq();
	            dto.setCompanyIntroId(companyIntroId);

	            // seq 세팅 후 insert 수행
	            n2 = profileMapper.insertCompanyIntroBasicInfo(dto);
	        }

	        if (n1 > 0 && n2 > 0) {
	            res.setSuccess(true);
	            res.setMessage("기본 정보가 저장되었습니다.");
	        } 
	        else {
	            res.setSuccess(false);
	            res.setMessage("기본 정보 저장에 실패했습니다.");
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	        res.setSuccess(false);
	        res.setMessage("기본 정보 저장 중 오류가 발생했습니다.");
	    }

	    return res;
	}
	
	//기업 주소정보 수정
	@Override
	@Transactional
	public CompanyProfileUpdateResponseDTO updateAddressProfile(CompanyProfileUpdateDTO dto) {
	    CompanyProfileUpdateResponseDTO res = new CompanyProfileUpdateResponseDTO();

	    try {
	        int n = profileMapper.updateCompanyAddressInfo(dto);

	        if (n > 0) {
	            res.setSuccess(true);
	            res.setMessage("주소 정보가 저장되었습니다.");
	        } else {
	            res.setSuccess(false);
	            res.setMessage("주소 정보 저장에 실패했습니다.");
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	        res.setSuccess(false);
	        res.setMessage("주소 정보 저장 중 오류가 발생했습니다.");
	    }

	    return res;
	}
	
	//기업 소개 수정
	@Override
	@Transactional
	public CompanyProfileUpdateResponseDTO updateIntroProfile(CompanyProfileUpdateDTO dto) {
	    CompanyProfileUpdateResponseDTO res = new CompanyProfileUpdateResponseDTO();

	    try {
	        int exists = profileMapper.existsCompanyIntro(dto.getMemberId());

	        int n;
	        if (exists > 0) {
	            // 이미 intro 행이 있으면 update 수행
	            n = profileMapper.updateCompanyIntroDetail(dto);
	        } 
	        else {
	            // intro 신규 생성 시 PK 시퀀스 값을 먼저 세팅
	            Long companyIntroId = profileMapper.getCompanyIntroSeq();
	            dto.setCompanyIntroId(companyIntroId);

	            // seq 세팅 후 insert 수행
	            n = profileMapper.insertCompanyIntroDetail(dto);
	        }

	        if (n > 0) {
	            res.setSuccess(true);
	            res.setMessage("기업 소개가 저장되었습니다.");
	        } 
	        else {
	            res.setSuccess(false);
	            res.setMessage("기업 소개 저장에 실패했습니다.");
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	        res.setSuccess(false);
	        res.setMessage("기업 소개 저장 중 오류가 발생했습니다.");
	    }

	    return res;
	}
	
	//기업 로고 등록
	@Override
	@Transactional
	public CompanyProfileUpdateResponseDTO uploadCompanyLogo(String memberId, MultipartFile logoFile) throws Exception {
	    CompanyProfileUpdateResponseDTO res = new CompanyProfileUpdateResponseDTO();

	    try {
	        if (logoFile == null || logoFile.isEmpty()) {
	            res.setSuccess(false);
	            res.setMessage("업로드할 로고 파일이 없습니다.");
	            return res;
	        }

	        // 1. company_intro_id 조회
	        Long companyIntroId = profileMapper.selectCompanyIntroIdByMemberId(memberId);
	        //System.out.println("companyIntroId:" +companyIntroId);

	        // 2. 없으면 빈 intro row 자동 생성
	        if (companyIntroId == null) {
	            companyIntroId = profileMapper.getCompanyIntroSeq();

	            CompanyProfileUpdateDTO dto = new CompanyProfileUpdateDTO();
	            dto.setCompanyIntroId(companyIntroId);
	            dto.setMemberId(memberId);

	            int introInsert = profileMapper.insertEmptyCompanyIntro(dto);

	            if (introInsert <= 0) {
	                throw new RuntimeException("기업 소개 기본 행 생성에 실패했습니다.");
	            }
	        }

	        // 3. 파일 업로드
	        byte[] bytes = logoFile.getBytes();
	        String originalFilename = logoFile.getOriginalFilename();
	        //System.out.println(originalFilename);

	        String savedFileName = fileManager.doFileUpload(
	                bytes,
	                originalFilename,
	                uploadPath + "/Logo"
	        );
	        //System.out.println(savedFileName);

	        if (savedFileName == null) {
	            throw new RuntimeException("기업 로고 업로드에 실패했습니다.");
	        }

	        String fileUrl = "images/Logo/" + savedFileName;
	        //System.out.println(fileUrl);

	        // 4. 기존 로고 조회
	        ImageFileDTO oldLogo = profileMapper.selectCompanyLogo(companyIntroId);
	        //System.out.println(oldLogo);

	        int n;
	        if (oldLogo != null) {
	            oldLogo.setFileUrl(fileUrl);
	            oldLogo.setOriginalFilename(originalFilename);
	            n = profileMapper.updateCompanyLogoFile(oldLogo);
	        } else {
	            Long fileId = profileMapper.getImageFileSeq();

	            ImageFileDTO imageDto = new ImageFileDTO();
	            imageDto.setFileId(fileId);
	            imageDto.setTargetId(companyIntroId);
	            imageDto.setTargetType("company");
	            imageDto.setFileCategory("logo");
	            imageDto.setFileUrl(fileUrl);
	            imageDto.setOriginalFilename(originalFilename);

	            n = profileMapper.insertImageFile(imageDto);
	        }

	        if (n > 0) {
	            res.setSuccess(true);
	            res.setMessage("기업 로고가 등록되었습니다.");
	            res.setLogoPath(fileUrl);
	        } else {
	            res.setSuccess(false);
	            res.setMessage("기업 로고 등록에 실패했습니다.");
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	        res.setSuccess(false);
	        res.setMessage("기업 로고 등록 중 오류가 발생했습니다.");
	    }

	    return res;
	}
	//========================= [기업 프로필] =========================//
	
	
	
	
	
	
	
	//채용공고 리스트 조회하기(다른 메서드에서 사용)
	/*
	@Override
	public List<JobPostingDTO> getJobPostingList(String memberId) {
		//List<JobPostingDTO> jobList = dao.getJobPostingList();
		return jobMapper.getJobPostingList(memberId);
	}
	*/
	// 신고 처리를 위한 채용공고 리스트 조회
	@Override
	public List<JobPostingDTO> getJobPostingList(String memberId) {
	    List<JobPostingDTO> jobList = jobMapper.getJobPostingList(memberId);

	    if (jobList != null) {
	        for (JobPostingDTO dto : jobList) {
	            dto.setReportStatusText(
	                convertReportProcessStatusText(dto.getReportProcessStatus())
	            );
	        }
	    }
	    return jobList;
	}
	

	//채용공고 리스트 조회하기(페이징처리)
	/*
	public List<JobPostingDTO> getJobPostingListPaing(Map<String, Object> paraMap) {
	    return jobMapper.getJobPostingListPaing(paraMap);
	}
	*/
	@Override
	public List<JobPostingDTO> getJobPostingListPaing(Map<String, Object> paraMap) {
	    List<JobPostingDTO> jobList = jobMapper.getJobPostingListPaing(paraMap);

	    if (jobList != null) {
	        for (JobPostingDTO dto : jobList) {
	            dto.setReportStatusText(
	                convertReportProcessStatusText(dto.getReportProcessStatus())
	            );
	        }
	    }

	    return jobList;
	}
	
	//공고 전체갯수
	@Override
	public int getJobPostingCount(String memberId) {
	    return jobMapper.getJobPostingCount(memberId);
	}
	
	
	//채용공고 삭제하기
	public int deleteJobPosting(Long jobId, String memberId) {
	    return jobMapper.deleteJobPosting(jobId, memberId);
	}
	
	
	// 2)선택된 공고 상세정보 조회하기
	/*
	@Override
	public JobPostingDTO getJobPostingOne(Long jobId) {
		return jobMapper.getJobPostingOne(jobId);
	}
	*/
	/*
	@Override
	public JobPostingDTO getJobPostingOne(Long jobId) {
	    JobPostingDTO dto = jobMapper.getJobPostingOne(jobId);

	    if (dto != null) {
	        dto.setReportStatusText(
	            convertReportProcessStatusText(dto.getReportProcessStatus())
	        );
	    }

	    return dto;
	}
	*/
	@Override
	public JobPostingDTO getJobPostingOne(Long jobId) {
	    JobPostingDTO dto = jobMapper.getJobPostingOne(jobId);

	    if (dto != null) {
	        dto.setReportStatusText(
	            convertReportProcessStatusText(dto.getReportProcessStatus())
	        );

	        dto.setSkillList(jobMapper.getSkillNamesByJobId(jobId));

	        // 혹시 템플릿에서 educationLevelName 을 쓰는데 값이 비어 있으면 보정
	        if (dto.getEducationLevelName() == null) {
	            dto.setEducationLevelName(dto.getEduLevelName());
	        }
	    }

	    return dto;
	}

	
	// 3)수정할 채용공고의 기존 내용 불러오기
	@Override
	public JobPostingEditResponseDTO getJobPostingForEdit(Long jobId) {
	    JobPostingEditResponseDTO dto = jobMapper.getJobPostingForEdit(jobId);
	    if(dto == null) return null;

	    // region chain
	    if(dto.getRegionCode() != null && !dto.getRegionCode().isBlank()){
	        RegionChainDTO chain = jobMapper.getRegionChain(dto.getRegionCode());
	        if(chain != null){
	            dto.setRegionLv1(chain.getRegionLv1());
	            dto.setRegionLv2(chain.getRegionLv2());
	            dto.setRegionLv3(chain.getRegionLv3());
	        }
	    }

	    // skills
	    dto.setSkillIds(jobMapper.getSkillIdsByJobId(jobId));

	    return dto;
	}
	
	// 3)채용공고 수정하기(공고 데이터 수정 및 기존 매핑테이블 값 삭제 후 다시 삽입)
	@Override
	@Transactional
	public int updateJobPosting(JobPostingDTO dto, List<Long> skillIds) {

	    JobPostingDTO origin = jobMapper.getJobPostingOne(dto.getJobId());

	    if (origin == null) {
	        return 0;
	    }

	    if (!origin.getMemberId().equals(dto.getMemberId())) {
	        return 0;
	    }

	    if (origin.getIsHidden() != null && origin.getIsHidden() == 1) {
	        return 0;
	    }
	    
        // 1) 공고 업데이트
	    int n = jobMapper.updateJobPosting(dto);
	    
        // 업데이트가 실패했으면 아래 매핑 작업을 안 하는게 안전
	    if (n != 1) return n;

        // 2) 기존 매핑 삭제
	    jobMapper.deleteJobPostingSkills(dto.getJobId());

        // 3) 새 매핑 insert (비어있으면 = 전부 해제)
	    if (skillIds != null && !skillIds.isEmpty()) {
	        jobMapper.insertNewJobPostingSkills(dto.getJobId(), skillIds);
	    }

	    return n;
	}
	

	
	//4)직무 및 기술스택 매핑테이블에 트랜잭션 처리하여 채용공고 등록하기
	@Override
	@Transactional
	public int insertJobPosting(JobPostingDTO dto, List<Long> skillIds) {
		//System.out.println("dto:" + dto);
		//System.out.println("skillIds" + skillIds);
		/*
		dto:JobPostingDTO(jobId=null, memberId=TESTC, categoryId=18, categoryName=null, 
						  regionCode=GG_SEONGNAM, title=임시저장 에러테스트, content=null, workType=정규직, 
						  careerType=무관, eduCode=EDU_NONE, eduLevelName=null, salary=null, headcount=1, 
						  status=임시저장, deadlineType=always, viewCount=null, scrapCount=null, isHidden=null, 
						  reportId=null, reportReasonId=null, reportReasonName=null, reportContent=null, 
						  reportProcessStatus=null, reportProcessReason=null, reportCreatedAt=null, reportProcessedAt=null, 
						  reportCount=null, reportStatusText=null, deadlineAt=null, openedAt=null, 
						  closedAt=null, createdAt=null, updatedAt=null, skillIds=null)
		skillIds[]
		*/
		
		//불러온 memberId 가 null 이라면 막아주기
		if(dto.getMemberId() == null){
		    throw new RuntimeException("로그인 정보가 없습니다.");
		}
		
		// 1) 공고 등록
	    int n = jobMapper.insertJobPosting(dto); 
	    if (n != 1) return n;

	    // 2) 방금 생성된 jobId가 dto에 세팅되어 있어야 매핑 insert 가능
	    Long jobId = dto.getJobId();
	    if (jobId == null) {
	        throw new IllegalStateException("insert 후 jobId가 DTO에 세팅되지 않았습니다.");
	    }

	    // 3) 기술스택 매핑 insert
	    if (skillIds != null && !skillIds.isEmpty()) {
	    	jobMapper.insertNewJobPostingSkills(jobId, skillIds);
	    }

	    return 1;
	}
	
	
	//공고 마감일/게시종료일에 따른 상태변화
	@Override
    @Transactional
    public void refreshJobPostingStatuses() {
        // 우선순위 중요
        jobMapper.updateJobStatusToDeleted();
        jobMapper.updateJobStatusToClosed();
        jobMapper.updateJobStatusToWaiting();
        jobMapper.updateJobStatusToOpen();
    }
	
	
	//학력 리스트 조회해오기
	@Override
	public List<EducationDTO> selectEduList() {
		return jobMapper.selectEduList();
	}


	//직무 대분류 리스트 가져오기
	public List<JobCategoryDTO> getRoots() {
        return jobMapper.selectRoots();
    }
	//직무 중분류 리스트 가져오기
    public List<JobCategoryDTO> getChildren(Long parentId) {
        return jobMapper.selectChildren(parentId);
    }

    
    //스킬 카테고리 조회해오기
    @Override
    public List<SkillCategoryDTO> getSkillCategoryWithSkills() {
        List<SkillJoinRowDTO> rows = jobMapper.selectSkillCategorySkillRows();

        // categoryId별로 묶기
        Map<Long, SkillCategoryDTO> map = new LinkedHashMap<>();

        for(SkillJoinRowDTO r : rows){

            SkillCategoryDTO cat = map.get(r.getSkillCategoryId());
            if(cat == null){
                cat = new SkillCategoryDTO();
                cat.setSkillCategoryId(r.getSkillCategoryId());
                cat.setCategoryName(r.getCategoryName());
                map.put(r.getSkillCategoryId(), cat);
            }

            // LEFT JOIN 이라 skillId가 null일 수 있음
            if(r.getSkillId() != null){
                SkillDTO s = new SkillDTO();
                s.setSkillId(r.getSkillId());
                s.setFkSkillCategoryId(r.getSkillCategoryId());
                s.setSkillName(r.getSkillName());
                cat.getSkills().add(s);
            }
        }

        return new ArrayList<>(map.values());
    }
	
	
    //지역 카테고리 조회해오기(대분류)
    @Override
    public List<RegionDTO> getRegionLevel1() {
        return jobMapper.selectRegionLevel1();
    }
    //중분류/소분류
    @Override
    public List<RegionDTO> getRegionChildren(String parentCode) {
        return jobMapper.selectRegionChildren(parentCode);
    }
    //========================= [직무] =========================//

    
    
    
    
    
    //========================= [지원자 관리] =========================//
    /*
    @Override
    public List<ApplicantListDTO> selectApplicantList(Map<String, Object> paraMap) {
        return applicantMapper.selectApplicantList(paraMap);
    }
    */
    // 지원자 목록 총 개수
    @Override
    public int selectApplicantCount(Map<String, Object> paraMap) {
        return applicantMapper.selectApplicantCount(paraMap);
    }

    // 지원자 목록 페이징 조회
    @Override
    public List<ApplicantListDTO> selectApplicantListPaging(Map<String, Object> paraMap) {
        return applicantMapper.selectApplicantListPaging(paraMap);
    }
    
    

    //지원자 상세를 클릭했을 때 읽었음으로 상태 변경
    @Override
    @Transactional
    public boolean readApplicantDetail(Map<String, Object> paraMap) {

        Integer currentStatus = applicantMapper.selectApplicantCurrentStatus(paraMap);

        if (currentStatus == null) {
            return false;
        }

        // 이미 열람 이상 상태면 추가 처리 안 함
        if (currentStatus != 0) {
            return true;
        }

        // 0(미열람) -> 1(열람)
        paraMap.put("prevStatus", 0);
        paraMap.put("newStatus", 1);

        int updateCount = applicantMapper.updateApplicantReadStatus(paraMap);
        if (updateCount != 1) {
            throw new RuntimeException("열람 처리 실패");
        }

        int historyCount = applicantMapper.insertApplicationHistory(paraMap);
        if (historyCount != 1) {
            throw new RuntimeException("열람 이력 저장 실패");
        }

        return true;
    }
    
    
    //지원자 상태 업데이트
    @Override
    @Transactional
    public boolean updateApplicantStatus(Map<String, Object> paraMap) {

        Integer currentStatus = applicantMapper.selectApplicantCurrentStatus(paraMap);

        if (currentStatus == null) {
            return false;
        }

        Integer prevStatus = (Integer) paraMap.get("prevStatus");
        Integer newStatus = (Integer) paraMap.get("newStatus");

        if (!currentStatus.equals(prevStatus)) {
            return false;
        }

        if (prevStatus.equals(newStatus)) {
            return false;
        }

        // 미열람 상태에서는 상세 진입으로만 열람 처리 가능
        if (currentStatus == 0) {
            return false;
        }

        int updateCount = applicantMapper.updateApplicantStatus(paraMap);
        if (updateCount != 1) {
            throw new RuntimeException("지원 상태 변경 실패");
        }

        int historyCount = applicantMapper.insertApplicationHistory(paraMap);
        if (historyCount != 1) {
            throw new RuntimeException("지원 상태 이력 저장 실패");
        }

        return true;
    }
    
    
    // 지원자 상세보기
    @Override
    public ApplicantDetailDTO getApplicantDetailForCompany(Map<String, Object> paraMap) {
        ApplicantDetailDTO dto = applicantMapper.selectApplicantDetailForCompany(paraMap);

        if (dto != null) {
            dto.setProcessStatusText(convertProcessStatusText(dto.getProcessStatus()));
        }

        return dto;
    }

    private String convertProcessStatusText(Integer processStatus) {
        if (processStatus == null) return "";

        switch (processStatus) {
            case 0: return "미열람";
            case 1: return "열람";
            case 2: return "서류탈락";
            case 3: return "면접요청";
            case 4: return "합격";
            case 5: return "불합격";
            default: return "";
        }
    }
    
    @Override
    public List<ImageFileDTO> getApplicationFiles(Long applicationId) {
        return applicantMapper.getApplicationFiles(applicationId);
    }

    @Override
    public List<Map<String, Object>> getApplicationTechstackList(Long submittedResumeId) {
        return applicantMapper.getApplicationTechstackList(submittedResumeId);
    }

    @Override
    public List<Map<String, Object>> getApplicationCertificateList(Long submittedResumeId) {
        return applicantMapper.getApplicationCertificateList(submittedResumeId);
    }
    //========================= [지원자 관리] =========================//
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    //========================= [제안서] =========================//
    //발송한 제안서 전체 통계 조회하기
    @Override
    public OfferMetricsSummaryDTO selectOfferMetricsSummary(String companyMemberId) {
        return offerMapper.selectOfferMetricsSummary(companyMemberId);
    }
    //발송한 제안서 상태 조회해오기
    @Override
    public List<OfferMetricsDTO> selectOfferMetricsByCompany(String companyMemberId) {
        return offerMapper.selectOfferMetricsByCompany(companyMemberId);
    }
    
    
    
    //구직자 정보 간단히 조회해오기
    @Override
    public List<MemberSimpleDTO> getReceiverList() {
        return offerMapper.selectJobSeekerList();
    }
    
    
	//제안서 리스트 조회해오기
	@Override
	public List<OfferListDTO> selectOfferList(String companyMemberId) {
		return offerMapper.selectOfferList(companyMemberId);
	}

	//제안서 상세정보 조회해오기
	@Override
	public OfferDetailDTO selectOfferDetail(Long id) {
		return offerMapper.selectOfferDetail(id);
	}
	
	
	//제안서 등록하기
    @Override
    @Transactional
    public Long createOfferLetter(OfferCreateRequestDTO req) {
        validateOfferLetterCommon(
                req.getJobId(),
                req.getTitle(),
                req.getMessage()
        );

        int n = offerMapper.insertOfferLetter(req);

        if (n != 1) {
            throw new IllegalStateException("제안서 등록에 실패했습니다.");
        }

        return req.getOfferLetterId();
    }
    
	
    //제안서 수정하기
    @Override
    @Transactional
    public int updateOfferLetter(OfferUpdateRequestDTO req) {
        if (req.getOfferLetterId() == null) {
            throw new IllegalArgumentException("제안서 번호가 없습니다.");
        }
        
        validateOfferLetterCommon(
                req.getJobId(),
                req.getTitle(),
                req.getMessage()
        );

        int n = offerMapper.updateOfferLetter(req);
        if (n == 0) {
            throw new IllegalStateException("삭제되었거나 존재하지 않는 제안서입니다.");
        }

        return n;
    }
	
    
    //제안서 삭제하기
    @Override
    @Transactional
    public int deleteOfferLetter(long offerLetterId, String companyMemberId) {
        // 1) 소유권 확인
        int owns = offerMapper.existsOfferLetterOwnedByCompany(offerLetterId, companyMemberId);
        if(owns != 1) return 0;

        // 2) 발송 이력 존재 여부(있으면 삭제 막기)
        /*
        int hasHistory = offerMapper.existsOfferSendHistory(offerLetterId);
        if(hasHistory == 1){
            // 여기서 예외를 던지면 컨트롤러에서 409로 처리 가능
            throw new IllegalStateException("발송 이력이 있는 제안서는 삭제할 수 없습니다.");
        }
        */

        // 3) 제안서 삭제
        return offerMapper.deleteOfferLetter(offerLetterId);
    }
    
    
	
	//제안서 발송 및 유효성 검사
    @Override
    @Transactional
    public Long sendOffer(OfferSendRequestDTO req, String companyMemberId) {

        // 1) 기본값 검사
        if (req == null || req.getOfferLetterId() == null) {
            throw new IllegalArgumentException("제안서 정보가 올바르지 않습니다.");
        }

        if (req.getExpireAt() == null || req.getExpireAt().trim().isEmpty()) {
            throw new IllegalArgumentException("만료일은 필수입니다.");
        }

        if (req.getReceiverMemberIds() == null || req.getReceiverMemberIds().isEmpty()) {
            throw new IllegalArgumentException("수신자를 1명 이상 선택하세요.");
        }

        // 2) 수신자 중복 제거 + 공백 제거
        List<String> receiverIds = req.getReceiverMemberIds().stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .distinct()
                .toList();

        if (receiverIds.isEmpty()) {
            throw new IllegalArgumentException("유효한 수신자가 없습니다.");
        }

        // 3) 제안서 + 연결 공고 정보 조회
        OfferSendValidationDTO info = offerMapper.selectOfferSendValidationInfo(req.getOfferLetterId());

        if (info == null) {
            throw new IllegalArgumentException("제안서를 찾을 수 없습니다.");
        }

        // 4) 소유권 검사
        if (!companyMemberId.equals(info.getCompanyMemberId())) {
            throw new SecurityException("해당 제안서를 발송할 권한이 없습니다.");
        }

        // 5) 공고 연결 여부 검사
        if (info.getJobId() == null) {
            throw new IllegalStateException("연결된 공고가 없는 제안서는 발송할 수 없습니다.");
        }

        LocalDate today = LocalDate.now();
        LocalDate expireDate = LocalDate.parse(req.getExpireAt());

        LocalDate openedAt = toLocalDate(info.getOpenedAt());
        LocalDate deadlineAt = toLocalDate(info.getDeadlineAt());
        LocalDate closedAt = toLocalDate(info.getClosedAt());

        // 6) 게시 시작 전 검사
        if (openedAt != null && today.isBefore(openedAt)) {
            throw new IllegalStateException("아직 게시 시작 전인 공고에는 제안서를 발송할 수 없습니다.");
        }

        // 7) 공고 마감일 경과 검사
        if (deadlineAt != null && today.isAfter(deadlineAt)) {
            throw new IllegalStateException("공고 마감일이 지난 공고에는 제안서를 발송할 수 없습니다.");
        }

        // 8) 게시 종료일 경과 검사
        if (closedAt != null && today.isAfter(closedAt)) {
            throw new IllegalStateException("게시 종료된 공고에는 제안서를 발송할 수 없습니다.");
        }

        // 9) 만료일 검사
        if (expireDate.isBefore(today)) {
            throw new IllegalArgumentException("만료일은 오늘 이전으로 설정할 수 없습니다.");
        }

        if (openedAt != null && expireDate.isBefore(openedAt)) {
            throw new IllegalArgumentException("만료일은 게시 시작일보다 빠를 수 없습니다.");
        }

        if (deadlineAt != null && expireDate.isAfter(deadlineAt)) {
            throw new IllegalArgumentException("만료일은 공고 마감일을 넘길 수 없습니다.");
        }

        if (closedAt != null && expireDate.isAfter(closedAt)) {
            throw new IllegalArgumentException("만료일은 게시 종료일을 넘길 수 없습니다.");
        }

        // 10) 수신자 유효성 검사
        int validCnt = offerMapper.countValidOfferReceivers(receiverIds);
        if (validCnt != receiverIds.size()) {
            throw new IllegalArgumentException("유효하지 않은 수신자가 포함되어 있습니다.");
        }

        // 11) 이미 발송한 회원 검사
        List<String> alreadySent =
                offerMapper.selectAlreadySentReceiverIds(req.getOfferLetterId(), receiverIds);

        if (alreadySent != null && !alreadySent.isEmpty()) {
            throw new IllegalStateException(
                    "이미 제안서를 발송한 회원이 포함되어 있습니다: " + String.join(", ", alreadySent)
            );
        }

        // 12) 날짜를 문자열이 아니라 java.sql.Date 로 변환해서 넘김
        java.sql.Date expireSqlDate = java.sql.Date.valueOf(expireDate);
        
        
        // 13) 발송 스냅샷 생성
        Map<String, Object> param = new HashMap<>();
        param.put("offerLetterId", req.getOfferLetterId());
        param.put("expireAt", expireSqlDate);

        offerMapper.insertOfferSubmitSnapshot(param);

        Long offerSubmitId = ((Number) param.get("offerSubmitId")).longValue();

        // 14) 수신자 저장
        offerMapper.insertOfferResponses(offerSubmitId, receiverIds);

        return offerSubmitId;
    }
    
    
    
    //제안서 생성/수정에 대한 검사
    private void validateOfferLetterCommon(Long jobId, String title, String message) {
    	//System.out.println(jobId);
        if (jobId == null) {
            throw new IllegalArgumentException("연결할 공고는 필수입니다.");
        }
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("제안서 제목은 필수입니다.");
        }
        if (title.trim().length() > 200) {
            throw new IllegalArgumentException("제안서 제목은 200자 이하여야 합니다.");
        }
        if (message == null || message.trim().isEmpty()) {
            throw new IllegalArgumentException("제안 메시지는 필수입니다.");
        }
    }
    
    
    
    
    //제안서를 발송한 회원(memberId) 목록 조회
    @Override
    public List<String> selectSentMemberIdsByOfferLetterId(Long offerLetterId, String companyMemberId) {
        int ownsTemplate = offerMapper.existsOfferLetterOwnedByCompany(offerLetterId, companyMemberId);
        int ownsHistory  = offerMapper.existsOfferHistoryOwnedByCompany(offerLetterId, companyMemberId);

        if (ownsTemplate != 1 && ownsHistory != 1) {
            throw new IllegalStateException("권한이 없거나 존재하지 않는 제안서입니다.");
        }

        return offerMapper.selectSentMemberIdsByOfferLetterId(offerLetterId);
    }
    
    
    //제안서 수신자 상세 조회
    @Override
    public List<OfferRecipientDetailDTO> selectOfferRecipientDetailsByOfferLetterId(Long offerLetterId, String companyMemberId) {

        int ownsTemplate = offerMapper.existsOfferLetterOwnedByCompany(offerLetterId, companyMemberId);
        int ownsHistory  = offerMapper.existsOfferHistoryOwnedByCompany(offerLetterId, companyMemberId);

        // 원본도 없고 발송 이력도 없으면 진짜 권한 없음 / 존재하지 않음
        if (ownsTemplate != 1 && ownsHistory != 1) {
            throw new IllegalStateException("권한이 없거나 존재하지 않는 제안서입니다.");
        }

        List<OfferRecipientDetailDTO> list = offerMapper.selectOfferRecipientDetailsByOfferLetterId(offerLetterId);

        if (list == null) {
            return Collections.emptyList();
        }

        for (OfferRecipientDetailDTO dto : list) {
            if (dto.getResponseStatus() != null) {
                switch (dto.getResponseStatus()) {
                    case 1:
                        dto.setResponseStatusText("수락");
                        break;
                    case 2:
                        dto.setResponseStatusText("거절");
                        break;
                    default:
                        dto.setResponseStatusText("미응답");
                        break;
                }
            } else {
                dto.setResponseStatusText("미응답");
            }
        }

        return list;
    }
    
    
    
 // 삭제된 원본 제안서 중 발송 이력이 있는 목록
    @Override
    public List<DeletedOfferHistoryDTO> selectDeletedOfferHistoryList(String companyMemberId) {
        return offerMapper.selectDeletedOfferHistoryList(companyMemberId);
    }
    
    //========================= [제안서] =========================//
    
    
    
    
    
    
    
    //========================= [결제] =========================//
    //결제 준비 메서드
    /*
	 authentication == null 체크
	 authentication.getName() 비어있는지 체크
	 insertPayment() 결과값 체크
	 주문번호 중복 가능성 대비 간단 재시도
     */
    @Override
    public PaymentReadyResponse preparePointCharge(PaymentReadyRequest req, Authentication authentication) {
        PaymentReadyResponse res = new PaymentReadyResponse();

        // 요청값 필수 체크
        if (req == null || req.chargeAmount == null) {
            res.ok = false;
            res.message = "chargeAmount가 필요합니다.";
            return res;
        }

        // 허용 금액만 결제 가능
        long ca = req.chargeAmount;
        if (!(ca == 100000 || ca == 200000 || ca == 500000)) {
            res.ok = false;
            res.message = "허용되지 않은 충전 금액입니다.";
            return res;
        }

        // 로그인 사용자 체크
        if (authentication == null || isBlank(authentication.getName())) {
            res.ok = false;
            res.message = "로그인 정보가 없습니다.";
            return res;
        }

        String memberId = authentication.getName();

        // 주문번호는 충돌 가능성을 고려해 최대 3번 시도
        String orderId = null;
        int insertCnt = 0;

        for (int i = 0; i < 3; i++) {
            orderId = generateOrderId();
            try {
                insertCnt = walletMapper.insertPayment(memberId, orderId, ca, "PENDING");
                if (insertCnt == 1) {
                    break;
                }
            } catch (Exception e) {
                // 주문번호 충돌/DB 오류 시 한 번 더 시도
                if (i == 2) {
                    throw e;
                }
            }
        }

        // 결제 준비 DB insert 검증
        if (insertCnt != 1) {
            res.ok = false;
            res.message = "결제 준비 중 오류가 발생했습니다.";
            return res;
        }

        res.ok = true;
        res.orderId = orderId;
        res.chargeAmount = ca;
        res.payAmount = 100L; // 프로젝트용 실제 결제 금액
        res.orderName = "포인트 충전(" + (ca / 10000) + "만원)";
        res.buyerName = "기업회원";
        res.buyerEmail = "";
        res.buyerTel = "";

        return res;
    }
    
    
    
    //결제 성공 후 메서드
    @Override
    public PaymentCompleteResponse completePointCharge(PaymentCompleteRequest req) {
        PaymentCompleteResponse res = new PaymentCompleteResponse();

        // 주문번호는 필수
        if (req == null || isBlank(req.merchantUid)) {
            res.ok = false;
            res.message = "merchantUid가 필요합니다.";
            return res;
        }

        return reconcileByOrderId(req.merchantUid, req.impUid);
    }
    @Override
    public PaymentCompleteResponse reconcilePointCharge(String orderId) {
        PaymentCompleteResponse res = new PaymentCompleteResponse();

        // 주문번호 필수 체크
        if (isBlank(orderId)) {
            res.ok = false;
            res.message = "orderId가 필요합니다.";
            return res;
        }

        return reconcileByOrderId(orderId, null);
    }
    private PaymentCompleteResponse reconcileByOrderId(String orderId, String reqImpUid) {
        PaymentCompleteResponse res = new PaymentCompleteResponse();
        
        

        // DB 주문 조회
        Map<String, Object> payment = walletMapper.selectPaymentByOrderId(orderId);
        if (payment == null) {
            res.ok = false;
            res.message = "주문번호가 존재하지 않습니다.";
            return res;
        }

        String status = String.valueOf(payment.get("STATUS"));
        Long chargeAmount = toLong(payment.get("CHARGE_AMOUNT"));
        String memberId = String.valueOf(payment.get("FK_MEMBERID"));

        // 이미 적립 완료된 주문이면 잔액만 다시 조회해서 반환
        if ("PAID".equalsIgnoreCase(status)) {
            Long balance = walletMapper.selectPointAvailableBalance(memberId);
            res.ok = true;
            res.orderId = orderId;
            res.paidAmount = chargeAmount;
            res.pointBalance = balance == null ? 0L : balance;
            res.message = "이미 처리된 결제입니다.";
            return res;
        }

        // 포트원 토큰 발급 실패는 FAILED가 아니라 VERIFYING
        String token = portOneV1Client.getAccessToken();
        if (isBlank(token)) {
            walletMapper.updatePaymentStatusIfCurrent(orderId, "PENDING", "VERIFYING");
            res.ok = false;
            res.orderId = orderId;
            res.message = "결제 확인 중입니다. 잠시 후 다시 확인해주세요.";
            return res;
        }

        // merchant_uid 기준 결제 조회
        PortOneV1Client.PortOnePaymentInfo info =
                portOneV1Client.getPaymentInfoByMerchantUid(token, orderId);

        // 포트원 일시 장애/네트워크 오류도 VERIFYING 처리
        if (info == null) {
            walletMapper.updatePaymentStatusIfCurrent(orderId, "PENDING", "VERIFYING");
            res.ok = false;
            res.orderId = orderId;
            res.message = "결제 확인 중입니다. 잠시 후 다시 확인해주세요.";
            return res;
        }

        // 주문번호 불일치는 실제 비정상 요청이므로 FAILED 처리
        if (!orderId.equals(info.merchantUid)) {
            walletMapper.updatePaymentStatusIfCurrent(orderId, "PENDING", "FAILED");
            res.ok = false;
            res.orderId = orderId;
            res.message = "주문번호가 일치하지 않습니다.";
            return res;
        }

        // 프론트에서 impUid를 넘겼다면 조회 결과와 일치하는지 한 번 더 확인
        if (!isBlank(reqImpUid) && !reqImpUid.equals(info.getImpUid())) {
            walletMapper.updatePaymentStatusIfCurrent(orderId, "PENDING", "FAILED");
            res.ok = false;
            res.orderId = orderId;
            res.message = "결제 고유번호가 일치하지 않습니다.";
            return res;
        }

        // 프로젝트용 실제 결제금액 100원 검증
        if (!Long.valueOf(100L).equals(info.amount)) {
            walletMapper.updatePaymentStatusIfCurrent(orderId, "PENDING", "FAILED");
            res.ok = false;
            res.orderId = orderId;
            res.message = "결제 금액이 일치하지 않습니다.";
            return res;
        }

        // 실제 결제 실패/취소인 경우만 FAILED 또는 CANCELED 처리
        if ("cancelled".equalsIgnoreCase(info.status) || "cancel".equalsIgnoreCase(info.status)) {
            walletMapper.updatePaymentStatusIfCurrent(orderId, "PENDING", "CANCELED");
            res.ok = false;
            res.orderId = orderId;
            res.message = "취소된 결제입니다.";
            return res;
        }

        if (!"paid".equalsIgnoreCase(info.status)) {
            walletMapper.updatePaymentStatusIfCurrent(orderId, "PENDING", "FAILED");
            res.ok = false;
            res.orderId = orderId;
            res.message = "결제가 완료 상태가 아닙니다: " + info.status;
            return res;
        }

        try {
        	//실제 적립 처리
            return applyChargePayment(orderId, memberId, chargeAmount, info);
        } catch (Exception e) {
            // 서버 내부 예외 발생 시 바로 FAILED로 확정하지 않고 확인 상태로 둠
            walletMapper.updatePaymentStatusIfCurrent(orderId, "PENDING", "VERIFYING");

            e.printStackTrace(); // 운영에서는 logger로 교체 권장

            res.ok = false;
            res.orderId = orderId;
            res.message = "결제는 완료되었을 수 있습니다. 잠시 후 다시 확인해주세요.";
            return res;
        }
    }
    @Transactional
    public PaymentCompleteResponse applyChargePayment(String orderId, String memberId, Long chargeAmount,
                                                      PortOneV1Client.PortOnePaymentInfo info) {
        PaymentCompleteResponse res = new PaymentCompleteResponse();

        // 지갑 조회
        Long walletId = walletMapper.selectPointWalletId(memberId);

        // 지갑이 없으면 생성
        if (walletId == null) {
            int walletInsertCnt = walletMapper.insertPointWallet(memberId);
            if (walletInsertCnt != 1) {
                throw new RuntimeException("포인트 지갑 생성 실패");
            }

            walletId = walletMapper.selectPointWalletId(memberId);
            if (walletId == null) {
                throw new RuntimeException("포인트 지갑 재조회 실패");
            }
        }

        // PENDING 상태일 때만 PAID 처리
        int paidUpdateCnt = walletMapper.updatePaymentPaidIfPending(
                orderId,
                info.payMethod,
                info.pgProvider,
                info.embPgProvider
        );

        if (paidUpdateCnt == 0) {
            // 다른 요청이 먼저 처리했을 가능성이 있으므로 재조회
            Map<String, Object> payment = walletMapper.selectPaymentByOrderId(orderId);
            String latestStatus = payment == null ? null : String.valueOf(payment.get("STATUS"));

            if ("PAID".equalsIgnoreCase(latestStatus)) {
                Long balance = walletMapper.selectPointAvailableBalance(memberId);
                res.ok = true;
                res.orderId = orderId;
                res.paidAmount = chargeAmount;
                res.pointBalance = balance == null ? 0L : balance;
                res.message = "이미 처리된 결제입니다.";
                return res;
            }

            throw new RuntimeException("결제 상태 변경 실패");
        }

        // 사용가능 포인트 적립
        int addPointCnt = walletMapper.addPointAvailable(memberId, chargeAmount);
        if (addPointCnt != 1) {
            throw new RuntimeException("포인트 적립 실패");
        }

        // 거래내역 기록
        int txInsertCnt = walletMapper.insertPointTransactionCharge(
                walletId,
                orderId,
                "CHARGE",
                "DONE",
                chargeAmount
        );
        if (txInsertCnt != 1) {
            throw new RuntimeException("포인트 거래내역 저장 실패");
        }

        Long balance = walletMapper.selectPointAvailableBalance(memberId);

        res.ok = true;
        res.orderId = orderId;
        res.paidAmount = chargeAmount;
        res.pointBalance = balance == null ? 0L : balance;
        res.message = "결제가 정상 반영되었습니다.";

        return res;
    }
    
    
    //사용자가 결제창에서 취소했을 때 남아버리는 대기 결제건을 바로 정리하는 용도
    @Override
    public PaymentCompleteResponse cancelPendingPayment(String orderId) {
        PaymentCompleteResponse res = new PaymentCompleteResponse();

        // 주문번호 필수 체크
        if (isBlank(orderId)) {
            res.ok = false;
            res.message = "orderId가 필요합니다.";
            return res;
        }

        // 주문 조회
        Map<String, Object> payment = walletMapper.selectPaymentByOrderId(orderId);
        if (payment == null) {
            res.ok = false;
            res.message = "존재하지 않는 주문입니다.";
            return res;
        }

        String status = String.valueOf(payment.get("STATUS"));

        // 이미 취소된 주문이면 그대로 성공 응답
        if ("CANCELED".equalsIgnoreCase(status)) {
            res.ok = true;
            res.orderId = orderId;
            res.message = "이미 취소 처리된 주문입니다.";
            return res;
        }

        // 이미 결제 완료된 주문이면 취소 정리 대상이 아님
        if ("PAID".equalsIgnoreCase(status)) {
            res.ok = false;
            res.orderId = orderId;
            res.message = "이미 결제가 완료된 주문은 취소 정리할 수 없습니다.";
            return res;
        }

        // PENDING 상태일 때만 CANCELED로 변경
        int updateCnt = walletMapper.updatePaymentStatusIfCurrent(orderId, "PENDING", "CANCELED");

        if (updateCnt == 1) {
            res.ok = true;
            res.orderId = orderId;
            res.message = "결제 취소가 정상 처리되었습니다.";
            return res;
        }

        // 상태가 이미 바뀐 경우 재조회해서 응답
        payment = walletMapper.selectPaymentByOrderId(orderId);
        String latestStatus = payment == null ? null : String.valueOf(payment.get("STATUS"));

        if ("CANCELED".equalsIgnoreCase(latestStatus)) {
            res.ok = true;
            res.orderId = orderId;
            res.message = "이미 취소 처리된 주문입니다.";
            return res;
        }

        if ("PAID".equalsIgnoreCase(latestStatus)) {
            res.ok = false;
            res.orderId = orderId;
            res.message = "이미 결제가 완료된 주문입니다.";
            return res;
        }

        res.ok = false;
        res.orderId = orderId;
        res.message = "결제 취소 처리 중 상태 변경에 실패했습니다.";
        return res;
    }
    
    
    //페이징 처리
    @Override
    public Map<String, Object> getWalletPageData(String memberId, String tab, int currentShowPageNo, int sizePerPage) {
        Map<String, Object> m = new HashMap<>();

        Long pointBalance = walletMapper.selectPointAvailableBalance(memberId);
        if (pointBalance == null) pointBalance = 0L;
        m.put("pointBalance", pointBalance);

        Map<String, Object> sum = walletMapper.selectPaymentSummary(memberId);
        if (sum == null) sum = Collections.emptyMap();
        m.put("paidTotal", toLong(sum.get("PAID_TOTAL")));
        m.put("pendingTotal", toLong(sum.get("PENDING_TOTAL")));
        m.put("cancelTotal", toLong(sum.get("CANCEL_TOTAL")));

        int startRno = ((currentShowPageNo - 1) * sizePerPage) + 1;
        int endRno = startRno + sizePerPage - 1;

        Map<String, Object> paraMap = new HashMap<>();
        paraMap.put("memberId", memberId);
        paraMap.put("startRno", startRno);
        paraMap.put("endRno", endRno);

        int totalCount = 0;

        if ("payment".equalsIgnoreCase(tab)) {
            totalCount = walletMapper.getPaymentCount(memberId);
            m.put("paymentList", walletMapper.selectPaymentListPaging(paraMap));
        } 
        else {
            totalCount = walletMapper.getPointTxCount(memberId);
            m.put("pointTxList", walletMapper.selectPointTxListPaging(paraMap));
        }

        m.put("totalCount", totalCount);
        m.put("currentShowPageNo", currentShowPageNo);
        m.put("sizePerPage", sizePerPage);

        return m;
    }
    
    
    
    // ===== utils는 서비스에 남겨도 OK =====
    private boolean isBlank(String s) { return s == null || s.trim().isEmpty(); }
    /*
    private Long toLong(Object o) {
        if (o == null) return null;
        if (o instanceof Number) return ((Number) o).longValue();
        return Long.valueOf(String.valueOf(o));
    }
    */
    private long toLong(Object v) {
        if (v == null) return 0L;
        if (v instanceof Number) return ((Number) v).longValue();
        try { return Long.parseLong(String.valueOf(v)); } catch (Exception e) { return 0L; }
    }
    //========================= [결제] =========================//
    
    
    
    
    
    
    
    //========================= [배너] =========================//
    @Override
    @Transactional
    public void insertBannerWithImage(BannerDTO bannerDto, MultipartFile bannerImage) throws Exception {

        // 1. 필수값 체크
        if (bannerDto == null) {
            throw new RuntimeException("배너 정보가 없습니다.");
        }

        if (bannerDto.getFkMemberId() == null || bannerDto.getFkMemberId().trim().isEmpty()) {
            throw new RuntimeException("회원 정보가 없습니다.");
        }

        if (bannerDto.getFkJobId() == null) {
            throw new RuntimeException("연결할 공고가 없습니다.");
        }

        if (bannerDto.getTitle() == null || bannerDto.getTitle().trim().isEmpty()) {
            throw new RuntimeException("배너 제목이 없습니다.");
        }

        if (bannerDto.getStartAt() == null || bannerDto.getStartAt().trim().isEmpty()) {
            throw new RuntimeException("시작일이 없습니다.");
        }

        // 2. 포인트 지갑 조회
        Map<String, Object> wallet = walletMapper.selectPointWalletByMemberId(bannerDto.getFkMemberId());
        if (wallet == null) {
            throw new RuntimeException("포인트 지갑이 없습니다. 먼저 포인트를 충전해 주세요.");
        }

        Long pointWalletId = toLong(wallet.get("POINT_WALLET_ID"));
        long availableBalance = toLong(wallet.get("AVAILABLE_BALANCE"));

        if (availableBalance < BANNER_AD_PRICE) {
            throw new RuntimeException("포인트가 부족합니다. 현재 포인트: " + availableBalance + "P");
        }

        // 배너 이미지는 반드시 첨부하도록 서비스에서도 한번 더 검증
        if (bannerImage == null || bannerImage.isEmpty()) {
            throw new RuntimeException("배너 이미지를 첨부해 주세요.");
        }
        
        // 시작일이 오늘 기준 2일 뒤부터만 가능하도록 서비스에서도 한번 더 검증
        LocalDate startDate = LocalDate.parse(bannerDto.getStartAt());
        LocalDate minStartDate = LocalDate.now().plusDays(2);

        if (startDate.isBefore(minStartDate)) {
            throw new RuntimeException("배너 등록 시작일은 " + minStartDate + "부터 선택할 수 있습니다.");
        }

        // 3. 시작일 기준 종료일 +7일 계산
        LocalDate endDate = startDate.plusDays(7);
        bannerDto.setEndAt(endDate.toString());

        // 4. 상태 고정
        bannerDto.setStatus("처리중");

        // 5. 포인트 차감 (원자적 처리)
        int updated = walletMapper.deductPointAvailable(bannerDto.getFkMemberId(), BANNER_AD_PRICE);
        if (updated == 0) {
            throw new RuntimeException("포인트 차감에 실패했습니다. 잔액을 다시 확인해 주세요.");
        }

        // 6. banner_id 채번
        Long bannerId = bannerMapper.getBannerSeq();
        bannerDto.setBannerId(bannerId);

        // 7. 배너 저장
        bannerMapper.insertBanner(bannerDto);

        // 8. 파일이 있으면 업로드 + image_file 저장
        if (bannerImage != null && !bannerImage.isEmpty()) {

            byte[] bytes = bannerImage.getBytes();
            String originalFilename = bannerImage.getOriginalFilename();

            String savedFileName = fileManager.doFileUpload(
                    bytes,
                    originalFilename,
                    uploadPath + "/Banner"
            );

            if (savedFileName == null) {
                throw new RuntimeException("배너 이미지 업로드에 실패했습니다.");
            }

            String fileUrl = "images/Banner/" + savedFileName;

            Long fileId = bannerMapper.getImageFileSeq();

            ImageFileDTO imageDto = new ImageFileDTO();
            imageDto.setFileId(fileId);
            imageDto.setTargetId(bannerId);
            imageDto.setTargetType("banner");
            imageDto.setFileCategory("-");
            imageDto.setFileUrl(fileUrl);
            imageDto.setOriginalFilename(originalFilename);

            bannerMapper.insertImageFile(imageDto);
            bannerMapper.updateBannerImageFileId(bannerId, fileId);
        }

        // 9. 포인트 사용 이력 저장
        walletMapper.insertPointTransactionBannerUse(
                pointWalletId,
                bannerId,
                "USE",
                "DONE",
                -BANNER_AD_PRICE
        );
    }
    
    //배너 등록 화면용 포인트 정보 조회 메서드
    @Override
    public Map<String, Object> getBannerPaymentInfo(String memberId) {
        Map<String, Object> result = new HashMap<>();

        Long pointBalance = walletMapper.selectPointAvailableBalance(memberId);
        if (pointBalance == null) {
            pointBalance = 0L;
        }

        result.put("bannerPrice", BANNER_AD_PRICE);
        result.put("pointBalance", pointBalance);
        result.put("canPay", pointBalance >= BANNER_AD_PRICE);

        return result;
    }


    //공고 목록 조회
	@Override
	public List<JobPostingDTO> getBannerPostingList(String memberId) {
		return bannerMapper.getBannerPostingList(memberId);
	}
	
	
	//배너 목록 조회
	/*
	@Override
	public List<BannerListDTO> getBannerListByMemberId(String memberId) {
	    return bannerMapper.selectBannerListByMemberId(memberId);
	}
	*/
	//배너 갯수 조회하기
	@Override
	public int getBannerCountByMemberId(String memberId) {
	    return bannerMapper.getBannerCountByMemberId(memberId);
	}
	//페이징처리를 위한 배너 리스트 조회하기
	@Override
	public List<BannerListDTO> getBannerListByMemberIdPaging(Map<String, Object> paraMap) {
	    return bannerMapper.selectBannerListByMemberIdPaging(paraMap);
	}
	

	//배너 종료일에 따른 상태변화
	@Override
	@Transactional
	public void refreshBannerStatuses() {
	    bannerMapper.updateBannerStatusToClosed();
	}
	
	
	// 마감된 배너 삭제 처리
	@Override
	@Transactional
	public boolean deleteBanner(Long bannerId, String memberId) {
	    int n = bannerMapper.deleteBannerByBannerId(bannerId, memberId);
	    return n > 0;
	}
    //========================= [배너] =========================//
    
    
	
	
	
	//========================= [인재검색] =========================//
	//직무조회
	@Override
    public List<JobCategoryDTO> getJobCategoryList() {
        return talentMapper.selectJobCategoryList();
    }
	//스킬카테고리 조회
    @Override
    public List<SkillCategoryDTO> getSkillCategoryList() {
        return talentMapper.selectSkillCategoryList();
    }
    //스킬 하위단 조회
    @Override
    public List<SkillDTO> getSkillList() {
        return talentMapper.selectSkillList();
    }
    
    //공개 대표이력서 목록
    @Override
    public List<TalentResumeDTO> getPublicPrimaryResumeList(TalentSearchConditionDTO searchDto) {
        List<TalentResumeDTO> list = talentMapper.selectPublicPrimaryResumeList(searchDto);

        for (TalentResumeDTO dto : list) {
            if (dto.getTechStackNamesRaw() != null && !dto.getTechStackNamesRaw().isBlank()) {
                dto.setTechStackNames(Arrays.asList(dto.getTechStackNamesRaw().split("\\|\\|")));
            }
        }

        return list;
    }
    
    //공개 대표이력서 수
    @Override
    public int getPublicPrimaryResumeCount(TalentSearchConditionDTO searchDto) {
        return talentMapper.selectPublicPrimaryResumeCount(searchDto);
    }
    
    //공개 대표이력서 상세
    @Override
    public TalentResumeDetailDTO getPublicPrimaryResumeDetail(Long resumeId) {
        TalentResumeDetailDTO resume = talentMapper.selectPublicPrimaryResumeDetail(resumeId);

        if (resume != null) {
            resume.setEducationList(talentMapper.selectTalentEducationList(resumeId));
            resume.setCareerList(talentMapper.selectTalentCareerList(resumeId));
            resume.setLanguageList(talentMapper.selectTalentLanguageList(resumeId));
            resume.setCertificateList(talentMapper.selectTalentCertificateList(resumeId));
            resume.setPortfolioList(talentMapper.selectTalentPortfolioList(resumeId));
            resume.setAwardList(talentMapper.selectTalentAwardList(resumeId));
            resume.setTechstackList(talentMapper.selectTalentTechstackList(resumeId));
        }

        return resume;
    }
    //========================= [인재검색] =========================//
	
    
    
    
    
    
    
}
