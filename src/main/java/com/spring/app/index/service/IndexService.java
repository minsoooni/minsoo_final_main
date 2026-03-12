package com.spring.app.index.service;

import java.util.List;
import java.util.Map;

public interface IndexService {
	//메인화면에 들어갈 이미지 정보 리스틀 가져오기
	List<Map<String, String>> getImageFileNameList();

}
