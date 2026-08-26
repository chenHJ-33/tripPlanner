package org.example.tripplanner.pojo.entity;

import lombok.Data;
import org.example.tripplanner.pojo.common.Location;

/**
 * 餐饮信息
 */
@Data
public class Meal {
    /** 餐饮类型: breakfast/lunch/dinner/snack */
    private String type;
    /** 餐饮名称 */
    private String name;
    /** 地址 */
    private String address;
    /** 经纬度坐标 */
    private Location location;
    /** 描述 */
    private String description;
    /** 预估费用(元)，默认: {@code 0} */
    private Integer estimatedCost = 0;
}