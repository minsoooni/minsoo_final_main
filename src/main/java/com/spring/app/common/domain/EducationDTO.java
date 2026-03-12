package com.spring.app.common.domain;

import lombok.Data;

@Data
public class EducationDTO {
	
	private String eduCode;	//학력코드
	private String eduName; //학력이름
	private Integer sort;  //학력정렬
	
}
