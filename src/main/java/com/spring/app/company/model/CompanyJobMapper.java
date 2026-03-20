package com.spring.app.company.model;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.spring.app.common.domain.EducationDTO;
import com.spring.app.common.domain.JobCategoryDTO;
import com.spring.app.common.domain.RegionDTO;
import com.spring.app.common.domain.SkillJoinRowDTO;
import com.spring.app.company.domain.JobPostingDTO;
import com.spring.app.company.domain.JobPostingEditResponseDTO;
import com.spring.app.company.domain.RegionChainDTO;

@Mapper
//채용공고 매퍼파일
public interface CompanyJobMapper {

	//채용공고 리스트 불러오기(페이징 처리 전 - 다른 메서드에서 사용)
	List<JobPostingDTO> getJobPostingList(String memberId);
	
	//채용공고 리스트 불러오기(페이징용)
	List<JobPostingDTO> getJobPostingListPaing(Map<String, Object> paraMap);
	int getJobPostingCount(String memberId); //공고 전체갯수
	
	//채용공고 삭제하기
	//int deleteJobPosting(Long jobId);
	int deleteJobPosting(@Param("jobId") Long jobId,
            @Param("memberId") String memberId);
	
	//선택한 공고 상세정보
	JobPostingDTO getJobPostingOne(Long jobId);
	
	// 팝업용 기술스택명 조회
	List<String> getSkillNamesByJobId(Long jobId);
	
	
	//기존 공고의 값 조회하기(상세내용/지역/스킬)
	JobPostingEditResponseDTO getJobPostingForEdit(long jobId);
	RegionChainDTO getRegionChain(String regionCode);
	List<Long> getSkillIdsByJobId(long jobId);
	
	
	//채용공고 수정하기
	int updateJobPosting(JobPostingDTO dto);
	//기존 기술스택-채용공고 데이터 삭제
	int deleteJobPostingSkills(@Param("jobId") long jobId);
	
	//새로운 기술스택-채용공고 데이터 삽입 == 공고 등록 시에도 이 메서드를 사용
	int insertNewJobPostingSkills(@Param("jobId") long jobId,
	                    @Param("skillIds") List<Long> skillIds);
	
	
	
	//채용공고 등록하기 => 트랜잭션 처리하기(공고-기술스택 매핑테이불 삽입은 위의 insertNewJobPostingSkills 사용)
	int insertJobPosting(JobPostingDTO dto); // insert 후 dto.jobId 채워지게
	
	
	//학력 리스트 조회해오기
	List<EducationDTO> selectEduList();
	

	//직무 리스트 조회해오기
	List<JobCategoryDTO> selectRoots(); //대분류
    List<JobCategoryDTO> selectChildren(Long parentId); //중분류
    
    //스킬카테고리 조회해오기
	Object getSkillCategoryWithSkills();
	
	//카테고리 + 스킬 조인 결과(1번 쿼리)
    List<SkillJoinRowDTO> selectSkillCategorySkillRows();
    
    
    //지역 리스트 조회해오기
    List<RegionDTO> selectRegionLevel1();
    List<RegionDTO> selectRegionChildren(@Param("parentCode") String parentCode);

    
    int updateJobStatusToClosed();   // 마감 처리
    int updateJobStatusToDeleted();  // 삭제됨 처리
    int updateJobStatusToOpen();     // 게시중 처리
    int updateJobStatusToWaiting();  // 대기 처리

    
    
    
   

}
