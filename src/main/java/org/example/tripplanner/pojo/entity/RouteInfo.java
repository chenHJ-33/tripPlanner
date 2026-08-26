package org.example.tripplanner.pojo.entity;

import lombok.Data;

/**
 * 路线信息
 */
@Data
public class RouteInfo {
    /** 距离(米) */
    private Double distance;
    /** 时间(秒) */
    private Integer duration;
    /** 路线类型 */
    private String routeType;
    /** 路线描述 */
    private String description;
}