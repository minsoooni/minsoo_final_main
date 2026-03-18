package com.spring.app.index.model;

import java.util.List;
import java.util.Map;

public interface IndexDAO {
	//메인화면에 들어갈 이미지 정보 리스틀 가져오기
	List<Map<String, String>> getImageFileNameList();

	// === 직무 부모 카테고리 조회 ===
	List<Map<String, Object>> selectParentJobCategories();
	
	// 핫한 기업 조회
	List<Map<String, Object>> selectHotCompanies();
	
	// 메인 스폰서 배너 목록 조회
    List<Map<String, Object>> selectMainBannerList();
}
