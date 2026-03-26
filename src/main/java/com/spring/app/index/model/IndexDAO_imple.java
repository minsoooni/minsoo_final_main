package com.spring.app.index.model;

import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class IndexDAO_imple implements IndexDAO {
	@Qualifier("sqlsession")
	private final SqlSessionTemplate sqlSession;
	
	@Override
	public List<Map<String, String>> getImageFileNameList() {
		return sqlSession.selectList("index.getImgFileNameList");
	}
	
	 // === 직무 부모 카테고리 조회 ===
    @Override
    public List<Map<String, Object>> selectParentJobCategories() {
        return sqlSession.selectList("index.selectParentJobCategories");
    }
	
    // 핫한 기업 조회 
    @Override
    public List<Map<String, Object>> selectHotCompanies() {
        return sqlSession.selectList("index.selectHotCompanies");
    }
	
    @Override
	public List<Map<String, Object>> selectMainBannerList() {
		return sqlSession.selectList("index.selectMainBannerList");
	}

}
