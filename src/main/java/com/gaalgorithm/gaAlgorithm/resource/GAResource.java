package com.gaalgorithm.gaAlgorithm.resource;

import com.gaalgorithm.gaAlgorithm.services.GAService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class GAResource {

    private final GAService service;

    @GetMapping("/{reproductionRate}/{probabilityMutation}/{populationLimit}/{storageLimit}")
    public String ga( @PathVariable("reproductionRate") Integer reproductionRate,
                        @PathVariable("probabilityMutation") Integer probabilityMutation,
                        @PathVariable("populationLimit") Integer populationLimit,
                        @PathVariable("storageLimit") Integer storageLimit) {
         service.start(reproductionRate, probabilityMutation, populationLimit, storageLimit);
        return "wait for the reply in your email";
    }

    @GetMapping("/test")
    public String test() {
        return "Server ok";
    }
}
