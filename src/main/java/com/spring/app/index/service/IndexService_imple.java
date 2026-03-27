package com.spring.app.index.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.spring.app.index.model.IndexDAO;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class IndexService_imple implements IndexService {
	private final IndexDAO indexDAO;

	
	//메인화면에 들어갈 이미지 정보 리스틀 가져오기
	@Override
	public List<Map<String, String>> getImageFileNameList() {
		return indexDAO.getImageFileNameList();
	}
	
	// === 직무 부모 카테고리 조회 ===
    @Override
    public List<Map<String, Object>> getParentJobCategories() {
        return indexDAO.selectParentJobCategories();
    }
    
    // 핫한기업 조회
    @Override
    public List<Map<String, Object>> getHotCompanies() {
        return indexDAO.selectHotCompanies();
    }

    @Override
	public List<Map<String, Object>> getMainBannerList() {
		return indexDAO.selectMainBannerList();
	}
}
