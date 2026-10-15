package br.edu.atitus.currencyapi.dtos.bcb;
import java.util.List;

public record BcbResponse(List<BcbValue> value) {
}