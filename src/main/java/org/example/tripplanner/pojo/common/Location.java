package org.example.tripplanner.pojo.common;

import lombok.Data;

/**
 * 地理位置
 */
@Data
public class Location {

    /**
     * 经度
     */
    private Double longitude;

    /**
     * 纬度
     */
    private Double latitude;
}