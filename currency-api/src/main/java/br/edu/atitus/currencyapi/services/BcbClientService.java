package br.edu.atitus.currencyapi.services;

import br.edu.atitus.currencyapi.clients.BcbClient;
import br.edu.atitus.currencyapi.dtos.bcb.BcbResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class BcbClientService {

    private final BcbClient bcbClient;

    public BcbClientService(BcbClient bcbClient) {
        this.bcbClient = bcbClient;
    }

    @Cacheable(value = "bcb-api", key = "#moeda + '-' + #dataCotacao")
    @Retry(name = "bcb-api")
    @CircuitBreaker(name = "bcb-api")
    public double buscarCotacaoBCB(String moeda, String dataCotacao) throws Exception {
        // A API do BCB exige aspas simples em volta dos parâmetros
        String moedaComAspas = "'" + moeda + "'";
        String dataComAspas = "'" + dataCotacao + "'";

        BcbResponse response = bcbClient.getCotacao(moedaComAspas, dataComAspas, "json");



        if (response.value() == null || response.value().isEmpty()) {
            throw new Exception("Cotação não encontrada no BCB");
        }

        // Retorna a cotação de venda do primeiro item da lista
        return response.value().get(0).cotacaoVenda();
    }
}