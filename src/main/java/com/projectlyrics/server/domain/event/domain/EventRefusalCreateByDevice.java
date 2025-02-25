package com.projectlyrics.server.domain.event.domain;

public record EventRefusalCreateByDevice(
        Event event,
        String deviceId
) {
}
