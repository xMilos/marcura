package com.marcura.test.service;

import com.marcura.test.exception.ConversionNotFoundException;
import com.marcura.test.model.Rates;
import com.marcura.test.model.Spread;
import com.marcura.test.model.response.CurrencyExchangeResponse;
import com.marcura.test.model.response.FixerResponse;
import com.marcura.test.repository.RatesRepository;
import com.marcura.test.repository.SpreadRepository;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ExchangeService {

  @Value("${fixerio.apikey}")
  private String APIKEY;
  @Value("${fixerio.latest.url}")
  private String URL;

  final private RestTemplate restTemplate;
  final private RatesRepository ratesRepository;
  final private SpreadRepository spreadRepository;

  @Autowired
  public ExchangeService(RestTemplate restTemplate, RatesRepository ratesRepository, SpreadRepository spreadRepository) {
    this.restTemplate = restTemplate;
    this.ratesRepository = ratesRepository;
    this.spreadRepository = spreadRepository;
  }

  @SneakyThrows
  @Transactional
  public CurrencyExchangeResponse getExchangeRates(String from, String to, LocalDate date) {
    if (date == null) {
      date = ratesRepository.findLatestDate();
    }
    Rates fromCurrency = ratesRepository.findByCurrencyAndDate(from, date);
    if (fromCurrency == null) {
      throw new ConversionNotFoundException();
    }
    Rates toCurrency = ratesRepository.findByCurrencyAndDate(to, date);
    if (toCurrency == null) {
      throw new ConversionNotFoundException();
    }
    updateCounters(fromCurrency, toCurrency);

    Spread fromCurrencySpread = spreadRepository.findByCurrency(from)
      .orElse(spreadRepository.findByCurrency("ELSE").get());
    Spread toCurrencySpread = spreadRepository.findByCurrency(to)
      .orElse(spreadRepository.findByCurrency("ELSE").get());

    BigDecimal exchange = calculateExchangeWithSpread(fromCurrency,
      toCurrency, fromCurrencySpread, toCurrencySpread);

    return new CurrencyExchangeResponse(from, to, exchange);
  }

  public BigDecimal calculateExchangeWithSpread(Rates fromCurrencyExchange, Rates toCurrencyExchange,
                                                 Spread fromCurrencySpread, Spread toCurrencySpread) {

    return toCurrencyExchange.getConversion()
      .divide(fromCurrencyExchange.getConversion(), 20, RoundingMode.HALF_UP)
      .multiply(
        BigDecimal.valueOf(100)
          .subtract(
            toCurrencySpread.getPercentage().max(fromCurrencySpread.getPercentage())
          )
      ).divide(BigDecimal.valueOf(100),20, RoundingMode.HALF_UP)
      .setScale(20, RoundingMode.HALF_UP);
  }

  private void updateCounters(Rates fromCurrencyExchange, Rates toCurrencyExchange) {
    fromCurrencyExchange.setCounter(fromCurrencyExchange.getCounter() + 1);
    ratesRepository.save(fromCurrencyExchange);
    toCurrencyExchange.setCounter(toCurrencyExchange.getCounter() + 1);
    ratesRepository.save(toCurrencyExchange);
  }

  @Transactional
  public List<Long> saveOrUpdateExchange() {
    FixerResponse fixerResponse = getExchangesFromFixer();
    Map<Rates, Rates> ratesFromDb = ratesRepository.findByDate(fixerResponse.getDate()).stream().collect(Collectors.toMap(r -> r, r -> r));
    Set<Rates> fixerResponseToRates = fixerResponse.getRates().entrySet().stream()
      .map(it -> new Rates(it.getKey(), it.getValue(), fixerResponse.getDate(), 0L)
      ).collect(Collectors.toSet());
    if (ratesFromDb.isEmpty()) {
      return ratesRepository.saveAll(fixerResponseToRates).stream().map(Rates::getId).collect(Collectors.toList());
    } else {
      List<Rates> updateExchange = fixerResponseToRates.stream().map(it -> {
          Rates rates = ratesFromDb.get(it);
          rates.setConversion(it.getConversion());
          return rates;
        }
      ).collect(Collectors.toList());
      return ratesRepository.saveAll(updateExchange).stream().map(Rates::getId).collect(Collectors.toList());
    }
  }

  public FixerResponse getExchangesFromFixer() {
    MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
    headers.add("apikey", APIKEY);
    log.info("Getting response from fixer");
    FixerResponse fixerResponse = restTemplate.exchange(URL, HttpMethod.GET, new HttpEntity<>(headers), FixerResponse.class).getBody();
    log.info("Received response from fixer");
    return fixerResponse;
  }
}
