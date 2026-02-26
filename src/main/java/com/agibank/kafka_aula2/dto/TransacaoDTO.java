package com.agibank.kafka_aula2.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record TransacaoDTO(
        String id,
        String contaId,
        String cartaoId,
        BigDecimal valor,
        String comerciante,
        String localizacao,
        String tipoTransacao,
        OffsetDateTime dataHora
) {
}
