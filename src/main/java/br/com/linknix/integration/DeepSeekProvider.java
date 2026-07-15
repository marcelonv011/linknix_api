package br.com.linknix.integration;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DeepSeekProvider extends AbstractLLMProviderSimulado {

    public DeepSeekProvider() {
        super("DEEPSEEK", new BigDecimal("0.89"), 100L);
    }
}
