package com.bibhu.learn.kafka.trackingservice.message;

import com.bibhu.learn.kafka.trackingservice.service.TrackingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TrackingStatusUpdated {

    UUID orderId;

    TrackingStatus status;
}
