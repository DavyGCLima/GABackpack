package com.gaalgorithm.gaAlgorithm.services;

import com.gaalgorithm.gaAlgorithm.services.dto.EmailDTO;
import com.gaalgorithm.gaAlgorithm.services.dto.RequestParamsDTO;
import com.gaalgorithm.gaAlgorithm.services.rabbit.ConsumidorTarget;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
@EnableBinding(ConsumidorTarget.class)
public class ConsumerService {

    private final EmailServico emailServico;

    private final GAService gaService;

    @StreamListener(target = ConsumidorTarget.BINDING_MAILER)
    public void sendMail(@Payload EmailDTO emailDTO) {
        log.info("Evento recebido {}", emailDTO);
        emailServico.sendMail(emailDTO);
    }

    @StreamListener(target = ConsumidorTarget.BINDING_GA)
    public void initialize(@Payload RequestParamsDTO requestParamsDTO) {
        log.info("Evento recebido {}", requestParamsDTO);
        gaService.start(requestParamsDTO);
    }
}
