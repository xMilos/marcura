package com.marcura.test.model.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@Data
public class FixerResponse {

  private String base;
  private LocalDate date;
  private Map<String, BigDecimal> rates;
  private Boolean success;
  private Long timestamp;
}
