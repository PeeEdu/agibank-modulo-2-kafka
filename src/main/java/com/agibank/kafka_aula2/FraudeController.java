package com.agibank.kafka_aula2;

import com.agibank.kafka_aula2.dto.TransacaoDTO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/fraude")
public class FraudeController {

    private final FraudeService fraudeService;

    public FraudeController(FraudeService fraudeService) {
        this.fraudeService = fraudeService;
    }

    @PostMapping
    public boolean verificarTransacao(@RequestBody TransacaoDTO transacao) {
        return fraudeService.isFraude(transacao);
    }
}