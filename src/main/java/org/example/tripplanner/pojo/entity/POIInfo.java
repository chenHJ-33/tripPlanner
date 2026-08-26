package org.example.tripplanner.pojo.entity;

import lombok.Data;
import org.example.tripplanner.pojo.common.Location;

/**
 * POI信息
 */
@Data
public class POIInfo {
    /** POI ID */
    private String id;
    /** 名称 */
    private String name;
    /** 类型 */
    private String type;
    /** 地址 */
    private String address;
    /** 经纬度坐标 */
    private Location location;
    /** 电话 */
    private String tel;
}