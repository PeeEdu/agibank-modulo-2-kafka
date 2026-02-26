package com.agibank.kafka_aula2;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/fraude")
public class FraudeController {

    private final FraudeService fraudeService;

    public FraudeController(FraudeService fraudeService) {
        this.fraudeService = fraudeService;
    }

    @GetMapping("/{chave}")
    public boolean verificarFraude(@PathVariable String chave){
        return fraudeService.isFraude(chave);
    }

}
