package com.gaalgorithm.gaAlgorithm.services.rabbit;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface ConsumidorTarget {

    String BINDING_MAILER = "mailer";

    String BINDING_GA = "gabackpack";

    @Input(ConsumidorTarget.BINDING_MAILER)
    SubscribableChannel email();

    @Input(ConsumidorTarget.BINDING_GA)
    SubscribableChannel initialize();

}

