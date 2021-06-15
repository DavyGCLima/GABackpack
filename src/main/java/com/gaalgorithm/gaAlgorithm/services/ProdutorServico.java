package com.gaalgorithm.gaAlgorithm.services;

import com.gaalgorithm.gaAlgorithm.domain.Item;
import com.gaalgorithm.gaAlgorithm.services.dto.EmailDTO;
import com.gaalgorithm.gaAlgorithm.services.dto.RequestParamsDTO;
import com.gaalgorithm.gaAlgorithm.services.rabbit.ProdutorSource;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.Optional;

@EnableBinding(ProdutorSource.class)
@Service
@RequiredArgsConstructor
@Slf4j
public class ProdutorServico {

  private final ProdutorSource produtorSource;

  public void enviarEmail( EmailDTO emailDTO ) {
    Message<EmailDTO> message = MessageBuilder.withPayload(emailDTO).build();
    produtorSource.enviarEmail().send(message);
  }

  public void initialize( RequestParamsDTO paramsDTO, MultipartFile file ) {
    List<Item> itemsFromFile = getItemsFromFile(file);
    paramsDTO.setItems(Optional.of(itemsFromFile));
    Message<RequestParamsDTO> message = MessageBuilder.withPayload(paramsDTO).build();
    produtorSource.initialize().send(message);
  }

  private List<Item> getItemsFromFile( MultipartFile file ) {
    if (file != null) {
      try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
        ColumnPositionMappingStrategy ms = new ColumnPositionMappingStrategy();
        ms.setType(Item.class);
        CsvToBean<Item> csvToBean = new CsvToBeanBuilder(reader).withType(Item.class).withSeparator(';').withIgnoreLeadingWhiteSpace(true).build();
        return csvToBean.parse();
      } catch (IOException e) {
        e.printStackTrace();
        log.error("Não foi possivel carregar o arquivo");
      }
    }
    return null;
  }

}
