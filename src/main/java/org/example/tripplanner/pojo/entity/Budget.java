package org.example.tripplanner.pojo.entity;

import lombok.Data;

/**
 * 预算信息
 */
@Data
public class Budget {
    /** 景点门票总费用，默认: {@code 0} */
    private Integer totalAttractions = 0;
    /** 酒店总费用，默认: {@code 0} */
    private Integer totalHotels = 0;
    /** 餐饮总费用，默认: {@code 0} */
    private Integer totalMeals = 0;
    /** 交通总费用，默认: {@code 0} */
    private Integer totalTransportation = 0;
    /** 总费用，默认: {@code 0} */
    private Integer total = 0;
}