package com.gaalgorithm.gaAlgorithm.resource;

import com.gaalgorithm.gaAlgorithm.services.GAService;
import com.gaalgorithm.gaAlgorithm.services.ProdutorServico;
import com.gaalgorithm.gaAlgorithm.services.dto.RequestParamsDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.print.attribute.standard.Media;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class GAResource {

  private final ProdutorServico produtorServico;

  @PostMapping(consumes = {MediaType.APPLICATION_OCTET_STREAM_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE,
    MediaType.APPLICATION_JSON_VALUE})
  public String ga2( @RequestParam(name = "reproductionRate") int reproductionRate, @RequestParam(name =
    "probabilityMutation") int probabilityMutation, @RequestParam(name = "populationLimit") int populationLimit,
                     @RequestParam(name = "storageLimit") int storageLimit,
                     @RequestParam(name = "selectionMode") int selectionMode,
                     @RequestParam(name = "reproductionMode") int reproductionMode, @RequestParam(name = "email",
    required = false) Optional<String> email, @RequestPart(name = "file", required = false) MultipartFile file,
                     @RequestParam Integer k, @RequestParam Integer y, @RequestParam Integer m ) {
    RequestParamsDTO paramsDTO = new RequestParamsDTO();
    paramsDTO.setReproductionMode(reproductionMode);
    paramsDTO.setReproductionRate(reproductionRate);
    paramsDTO.setProbabilityMutation(probabilityMutation);
    paramsDTO.setPopulationLimit(populationLimit);
    paramsDTO.setStorageLimit(storageLimit);
    paramsDTO.setSelectionMode(selectionMode);
    paramsDTO.setEmail(email.orElse(null));
    paramsDTO.setK(k);
    paramsDTO.setY(y);
    paramsDTO.setM(m);
    produtorServico.initialize(paramsDTO, file);
    return "Aguarde o resultado em seu email";
  }

  @PostMapping(path = "/bulk", consumes = {MediaType.APPLICATION_OCTET_STREAM_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE,
    MediaType.APPLICATION_JSON_VALUE})
  public String runBulkTest( @RequestParam List<Integer> reproductionRate, @RequestParam List<Integer> probabilityMutation,
                             @RequestParam List<Integer> populationLimit, @RequestParam List<Integer> storageLimit,
                             @RequestParam List<Integer> selectionMode, @RequestParam List<Integer> reproductionMode,
                             @RequestParam(required = false) Optional<String> email,
                             @RequestPart(required = false) MultipartFile file, @RequestParam List<Integer> k,
                             @RequestParam List<Integer> y, @RequestParam List<Integer> m ) {

    RequestParamsDTO paramsDTO = new RequestParamsDTO();
    paramsDTO.setBulkReproductionMode(reproductionMode);
    paramsDTO.setBulkReproductionRate(reproductionRate);
    paramsDTO.setBulkProbabiityMutation(probabilityMutation);
    paramsDTO.setBulkPopulationLimit(populationLimit);
    paramsDTO.setBulkStorageLimit(storageLimit);
    paramsDTO.setBulkSelectionMode(selectionMode);
    paramsDTO.setEmail(email.orElse(null));
    paramsDTO.setBulkK(k);
    paramsDTO.setBulkY(y);
    paramsDTO.setBulkM(m);
    if(paramsDTO.checkBulkParams()) {
      return "Parametros não combinam";
    }
    produtorServico.bulkInitialize(paramsDTO, file);
    return "Bateria de testes iniciada";
  }

  @GetMapping("/test")
  public String test() {
    return "Server ok";
  }
}
