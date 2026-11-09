package com.transitx.dto;

import jakarta.validation.constraints.NotNull;

public class BusPassApplyRequest {
    @NotNull(message = "Route ID is required")
    private Long routeId;

    @NotNull(message = "Boarding Stop ID is required")
    private Long boardingStopId;

    public BusPassApplyRequest() {}

    public Long getRouteId() { return routeId; }
    public void setRouteId(Long routeId) { this.routeId = routeId; }

    public Long getBoardingStopId() { return boardingStopId; }
    public void setBoardingStopId(Long boardingStopId) { this.boardingStopId = boardingStopId; }
}
