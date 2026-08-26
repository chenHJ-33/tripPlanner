package org.example.tripplanner.pojo.response;

import lombok.Data;
import org.example.tripplanner.pojo.entity.TripPlan;

/**
 * 旅行计划响应
 */
@Data
public class TripPlanResponse {
    /** 是否成功 */
    private Boolean success;
    /** 消息，默认: {@code ""} */
    private String message = "";
    /** 旅行计划数据 */
    private TripPlan data;
}