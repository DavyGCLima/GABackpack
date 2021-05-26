package com.gaalgorithm.gaAlgorithm.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "application.mail")
@Configuration
@Getter
@Setter
public class ApplicationProperties {

    private String enderecoRemetente;

    private String nomeRemetente;

}
