package com.gaalgorithm.gaAlgorithm.services;

import com.gaalgorithm.gaAlgorithm.services.dto.EmailDTO;
import com.gaalgorithm.gaAlgorithm.services.dto.RequestParamsDTO;
import com.gaalgorithm.gaAlgorithm.services.rabbit.ProdutorSource;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@EnableBinding(ProdutorSource.class)
@Service
@RequiredArgsConstructor
public class ProdutorServico {

    private final ProdutorSource produtorSource;

    public void enviarEmail(EmailDTO emailDTO) {
        Message<EmailDTO> message = MessageBuilder.withPayload(emailDTO).build();
        produtorSource.enviarEmail().send(message);
    }

    public void initialize(RequestParamsDTO paramsDTO) {
        Message<RequestParamsDTO> message = MessageBuilder.withPayload(paramsDTO).build();
        produtorSource.initialize().send(message);
    }
}
