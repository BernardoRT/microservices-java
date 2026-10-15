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
    private final BcbClientService bcbClientService;

    public CurrencyServiceJpa(CurrencyRepository repository, Environment env, BcbClientService bcbClientService) {
        this.repository = repository;
        this.env = env;
        this.bcbClientService = bcbClientService;
    }

    @Override
    public CurrencyResponse findBySourceCurrencyAndTargetCurrency(String sourceCurrency, String targetCurrency) throws Exception {
        String port = env.getProperty("local.server.port");
        String environmentStr = "Currency API running in Port: " + port;
        double conversionRate = 0.0;

        try {

            String moedaParaBuscar = sourceCurrency.equals("BRL") ? targetCurrency : sourceCurrency;
            conversionRate = bcbClientService.buscarCotacaoBCB(moedaParaBuscar, "15-10-2023");
            environmentStr += " (Fonte: Banco Central do Brasil)";

        } catch (Exception e) {

            CurrencyEntity entity = repository.findBySourceCurrencyAndTargetCurrency(sourceCurrency, targetCurrency)
                    .orElseThrow(() -> new Exception("Cotação não encontrada nem no BCB e nem no Banco de Dados Local"));

            conversionRate = entity.getConversionRate();
            environmentStr += " (Fonte: Banco de Dados Local - Fallback)";
        }

        return new CurrencyResponse(
                sourceCurrency,
                targetCurrency,
                conversionRate,
                environmentStr
        );
    }
}