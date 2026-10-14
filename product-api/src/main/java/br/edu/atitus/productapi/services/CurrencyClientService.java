package br.edu.atitus.productapi.services; // Se a sua pasta for apenas "services", tire o "impl" daqui

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;

// Se as duas linhas abaixo ficarem vermelhas, apague-as, digite Alt+Enter na palavra lá embaixo e importe!
import br.edu.atitus.productapi.clients.CurrencyClient;
import br.edu.atitus.productapi.dtos.CurrencyResponse;

@Service
public class CurrencyClientService {

    private final CurrencyClient currencyClient;

    public CurrencyClientService(CurrencyClient currencyClient) {
        this.currencyClient = currencyClient;
    }

    @Cacheable(value = "currency-api", key = "#source + '-' + #target")
    @Retry(name = "currency-api")
    @CircuitBreaker(name = "currency-api")
    public CurrencyResponse getCurrency(String source, String target) {
        return currencyClient.getCurrency(source, target);
    }
}