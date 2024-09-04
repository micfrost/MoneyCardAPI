package com.micfro.moneycard.model;

import org.springframework.data.annotation.Id;

public record MoneyCard(@Id Long id, Double amount, String owner) {

}
