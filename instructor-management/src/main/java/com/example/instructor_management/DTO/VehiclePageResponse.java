package com.example.instructor_management.DTO;

import java.util.List;

public class VehiclePageResponse {
    private List<VehicleAnalyticsDTO> content;

    public List<VehicleAnalyticsDTO> getContent() { return content; }
    public void setContent(List<VehicleAnalyticsDTO> content) { this.content = content; }
}
