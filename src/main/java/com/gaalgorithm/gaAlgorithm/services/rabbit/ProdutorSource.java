package com.gaalgorithm.gaAlgorithm.services.rabbit;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface ProdutorSource {

    String BINDING_MAILER = "mailer";

    String BINDING_GA = "gabackpack";

    @Output(ProdutorSource.BINDING_MAILER)
    MessageChannel enviarEmail();

    @Output(ProdutorSource.BINDING_GA)
    MessageChannel initialize();

}
