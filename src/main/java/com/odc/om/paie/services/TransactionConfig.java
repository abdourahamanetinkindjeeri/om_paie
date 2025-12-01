package com.odc.om.paie.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class TransactionConfig {

    @Bean
    public Map<String, TransactionStrategy> transactionStrategies(
            DepositStrategy depositStrategy,
            WithdrawalStrategy withdrawalStrategy,
            TransferStrategy transferStrategy) {

        Map<String, TransactionStrategy> strategies = new HashMap<>();
        strategies.put("deposit", depositStrategy);
        strategies.put("withdrawal", withdrawalStrategy);
        strategies.put("transfer", transferStrategy);
        return strategies;
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }
}