package com.bibhu.learn.kafka.trackingservice.handler;

import com.bibhu.learn.kafka.trackingservice.message.DispatchPreparing;
import com.bibhu.learn.kafka.trackingservice.service.TrackingService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@AllArgsConstructor
@Component
public class DispatchTrackingHandler {

    @Autowired
    private TrackingService trackingService;

    @KafkaListener(
            id = "dispatchTrackingConsumerClient",
            topics = "dispatch.tracking",
            groupId = "tracking.dispatch.tracking",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void listen(DispatchPreparing dispatchPreparing) throws Exception {
        try {
            trackingService.process(dispatchPreparing);;
        } catch (Exception e) {
            log.error("Processing failure", e);
        }
    }
}
