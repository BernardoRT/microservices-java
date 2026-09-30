package br.edu.atitus.currencyapi.repositories;

import br.edu.atitus.currencyapi.entities.CurrencyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CurrencyRepository extends JpaRepository {

    Optional findBySourceCurrencyAndTargetCurrency(String sourceCurrency, String targetCurrency);

}