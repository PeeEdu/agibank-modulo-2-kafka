package com.agibank.kafka_aula2.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDateTime;

public record MensagemRequest(
        @NotBlank(message = "O ID não pode ser vazio, nulo ou somente espaços")
        String id,

        @NotBlank(message = "O valor não pode ser vazio, nulo ou somente espaços")
        String valor,

        @NotBlank(message = "A data de processamento não pode ser vazio, nulo ou somente espaços")
        LocalDateTime dataDeProcessamento
) {
}
