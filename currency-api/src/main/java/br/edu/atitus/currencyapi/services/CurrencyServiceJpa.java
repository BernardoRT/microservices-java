package br.edu.atitus.currencyapi.services;

import br.edu.atitus.currencyapi.dtos.CurrencyResponse;
import br.edu.atitus.currencyapi.entities.CurrencyEntity;
import br.edu.atitus.currencyapi.repositories.CurrencyRepository;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
public class CurrencyServiceJpa implements CurrencyService {

    private final CurrencyRepository repository;
    private final Environment env;

    public CurrencyServiceJpa(CurrencyRepository repository, Environment env) {
        this.repository = repository;
        this.env = env;
    }

    @Override
    public CurrencyResponse findBySourceCurrencyAndTargetCurrency(String sourceCurrency, String targetCurrency) throws Exception {
        // Busca no banco
        CurrencyEntity entity = repository.findBySourceCurrencyAndTargetCurrency(sourceCurrency, targetCurrency)
                .orElseThrow(() -> new Exception("Cotação não encontrada"));

        // Captura a porta do ambiente
        String port = env.getProperty("local.server.port");
        String environmentStr = "Currency API running in Port: " + port;


        return new CurrencyResponse(
                entity.getSourceCurrency(),
                entity.getTargetCurrency(),
                entity.getConversionRate(),
                environmentStr
        );
    }
}