package org.example.tripplanner.pojo.response;

import lombok.Data;

@Data
public class HealthResponse {
    private String status;
    private String service;
    private int mapTollsCount;
}
