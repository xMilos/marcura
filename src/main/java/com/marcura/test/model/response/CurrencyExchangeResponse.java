package com.marcura.test.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@Schema(name = "Currency exchange response", description = "Response from exchange of FROM -> TO currency")
public class CurrencyExchangeResponse {
    @Schema(
      example = "USD"
    )
    private String from;
    @Schema(
      example = "EUR"
    )
    private String to;
    @Schema(
      example = "0.95"
    )
    private BigDecimal exchange;
}
