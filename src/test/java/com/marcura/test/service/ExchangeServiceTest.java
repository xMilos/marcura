package com.marcura.test.service;

import com.marcura.test.model.Rates;
import com.marcura.test.model.Spread;
import com.marcura.test.repository.RatesRepository;
import com.marcura.test.repository.SpreadRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.*;

class ExchangeServiceTest {

  @MockBean
  private RestTemplate restTemplate;
  @MockBean
  private RatesRepository ratesRepository;
  @MockBean
  private SpreadRepository spreadRepository;

  @InjectMocks
  private ExchangeService exchangeService = new ExchangeService(restTemplate, ratesRepository, spreadRepository);

  @Test
  void calculateExchangeWithSpread() {

    Rates fromCurrencyExchange = new Rates();
    fromCurrencyExchange.setConversion(BigDecimal.valueOf(0.8));
    Rates toCurrencyExchange = new Rates();
    toCurrencyExchange.setConversion(BigDecimal.valueOf(3.7));
    Spread fromCurrencySpread = new Spread();
    fromCurrencySpread.setPercentage(new BigDecimal(1));
    Spread toCurrencySpread = new Spread();
    toCurrencySpread.setPercentage(new BigDecimal(4));
    BigDecimal response = exchangeService.calculateExchangeWithSpread(fromCurrencyExchange, toCurrencyExchange, fromCurrencySpread, toCurrencySpread);
    BigDecimal expectedResponse = BigDecimal.valueOf(4.44);
    Assertions.assertEquals(expectedResponse, response.setScale(2, RoundingMode.HALF_UP));
  }
}