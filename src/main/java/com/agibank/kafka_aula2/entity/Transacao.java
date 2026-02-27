package com.agibank.kafka_aula2.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class Transacao {
    String id;
    String contaId;
    String cartaoId;
    BigDecimal valor;
    String comerciante;
    String localizacao;
    String tipoTransacao;
    OffsetDateTime dataHora;
}
