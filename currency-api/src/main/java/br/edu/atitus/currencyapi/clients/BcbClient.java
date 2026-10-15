package br.edu.atitus.currencyapi.clients;

import br.edu.atitus.currencyapi.dtos.bcb.BcbResponse;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;

public interface BcbClient {

    @GetExchange("/olinda/servico/PTAX/versao/v1/odata/CotacaoMoedaDia(moeda=@moeda,dataCotacao=@dataCotacao)")
    BcbResponse getCotacao(
            @RequestParam("@moeda") String moeda,
            @RequestParam("@dataCotacao") String dataCotacao,
            @RequestParam("$format") String format
    );
}