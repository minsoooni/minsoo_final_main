package com.spring.app.jobseeker.domain;

import lombok.Data;

@Data
public class RegionDTO {

    private String regionCode;          // 지역 코드
    private String parentRegionCode;    // 상위 지역
    private String regionName;          // 지역명
    private int regionLevel;            // 지역 단계 (1:서울시/경기도, 2:구(서울)/시,군(경기), 3:구(경기))
    private int sort;                   // 정렬순서 (각 지역단계별 가나다순)
}
