package com.gaalgorithm.gaAlgorithm.resource;

import com.gaalgorithm.gaAlgorithm.services.GAService;
import com.gaalgorithm.gaAlgorithm.services.dto.RequestParamsDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class GAResource {

  private final GAService service;

  @GetMapping("/{reproductionRate}/{probabilityMutation}/{populationLimit}/{storageLimit}/{selectionMode}")
  public String ga( @PathVariable("reproductionRate") Integer reproductionRate,
                    @PathVariable("probabilityMutation") Integer probabilityMutation,
                    @PathVariable("populationLimit") Integer populationLimit,
                    @PathVariable("storageLimit") Integer storageLimit, @PathVariable Integer selectionMode, @PathVariable String email ) {
    service.start(reproductionRate, probabilityMutation, populationLimit, storageLimit, selectionMode, email);
    return "wait for the reply in your email";
  }

  @PostMapping
  public String ga2( @RequestBody RequestParamsDTO paramsDTO ) {
    service.start(paramsDTO.getReproductionRate(), paramsDTO.getProbabilityMutation(), paramsDTO.getPopulationLimit()
      , paramsDTO.getStorageLimit(), paramsDTO.getSelectionMode(), paramsDTO.getEmail());
    return "wait for the reply in your email";
  }

  @GetMapping("/test")
  public String test() {
    return "Server ok";
  }
}
