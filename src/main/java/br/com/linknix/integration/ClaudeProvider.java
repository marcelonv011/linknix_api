package br.com.linknix.integration;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class ClaudeProvider extends AbstractLLMProviderSimulado {

    public ClaudeProvider() {
        super("CLAUDE", new BigDecimal("0.92"), 140L);
    }
}
