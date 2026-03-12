package com.spring.app.common.domain;

import lombok.Data;

@Data
public class RegionDTO {
	private String regionCode;
    private String fkParentRegionCode;
    private String regionName;
    private int regionLevel;
    private int sort;
}
