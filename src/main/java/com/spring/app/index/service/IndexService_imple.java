package com.spring.app.index.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.spring.app.index.model.IndexDAO;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class IndexService_imple implements IndexService {
	private final IndexDAO dao;

	
	//메인화면에 들어갈 이미지 정보 리스틀 가져오기
	@Override
	public List<Map<String, String>> getImageFileNameList() {
		List<Map<String, String>> mapList = dao.getImageFileNameList();
		return mapList;
	}

}
