package com.gaalgorithm.gaAlgorithm.resource;

import com.gaalgorithm.gaAlgorithm.services.GAService;
import com.gaalgorithm.gaAlgorithm.services.ProdutorServico;
import com.gaalgorithm.gaAlgorithm.services.dto.RequestParamsDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class GAResource {

  private final ProdutorServico produtorServico;

  @PostMapping
  public String ga2( @RequestBody RequestParamsDTO paramsDTO) {
    produtorServico.initialize(paramsDTO);
    return "wait for the reply in your email";
  }

  @GetMapping("/test")
  public String test() {
    return "Server ok";
  }
}
