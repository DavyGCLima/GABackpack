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
    required = false) Optional<String> email,
                     @RequestPart(name = "file", required = false) MultipartFile file,
                     @RequestParam Integer k, @RequestParam Integer y, @RequestParam Integer m) {
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

  @GetMapping("/test")
  public String test() {
    return "Server ok";
  }
}
