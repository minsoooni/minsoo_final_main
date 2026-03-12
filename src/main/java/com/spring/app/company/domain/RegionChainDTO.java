package com.spring.app.company.domain;

import lombok.Data;


@Data
//공고 수정 시 기존 공고의 지역값 가져오는 DTO
public class RegionChainDTO {
	private String regionLv1;
	private String regionLv2;
	private String regionLv3;
}
