package com.agibank.kafka_aula2.entity;

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
