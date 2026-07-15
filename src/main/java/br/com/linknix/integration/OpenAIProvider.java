package br.com.linknix.integration;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class OpenAIProvider extends AbstractLLMProviderSimulado {

    public OpenAIProvider() {
        super("OPENAI", new BigDecimal("0.94"), 120L);
    }
}
