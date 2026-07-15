package br.com.linknix.integration;

import br.com.linknix.exception.IntegracaoException;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Component
public class LLMProviderRegistry {

    private final Map<String, LLMProvider> providers;

    public LLMProviderRegistry(List<LLMProvider> providers) {
        Map<String, LLMProvider> providersPorCodigo = new LinkedHashMap<>();

        for (LLMProvider provider : providers) {
            String codigo = normalizarCodigo(provider.obterCodigo());
            LLMProvider providerDuplicado = providersPorCodigo.putIfAbsent(
                    codigo,
                    provider
            );

            if (providerDuplicado != null) {
                throw new IllegalStateException(
                        "Existe mais de um provedor registrado com o código " + codigo
                );
            }
        }

        this.providers = Map.copyOf(providersPorCodigo);
    }

    public LLMProvider obter(String codigo) {
        String codigoNormalizado = normalizarCodigo(codigo);
        LLMProvider provider = providers.get(codigoNormalizado);

        if (provider == null) {
            throw new IntegracaoException(
                    "Não existe implementação para o provedor " + codigoNormalizado
            );
        }

        return provider;
    }

    private String normalizarCodigo(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            throw new IntegracaoException("O código do provedor é obrigatório");
        }

        return codigo.trim().toUpperCase(Locale.ROOT);
    }
}
