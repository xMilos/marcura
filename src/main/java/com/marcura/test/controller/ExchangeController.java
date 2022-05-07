package com.marcura.test.controller;

import com.marcura.test.exception.BadCurrencyException;
import com.marcura.test.model.response.CurrencyExchangeResponse;
import com.marcura.test.service.ExchangeService;
import com.marcura.test.util.CurrencyValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("v1")
@Tag(name = "exchange api", description = "Rest api for exchange")
@Slf4j
public class ExchangeController {

  private ExchangeService exchangeService;

  @Autowired
  public ExchangeController(ExchangeService exchangeService) {
    this.exchangeService = exchangeService;
  }

  @GetMapping("/exchange")
  @Operation(summary = "Endpoint to get conversion rate from base to desired currency for certain date")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Exchange successful",
      content = @Content(
        mediaType = "application/json",
        schema = @Schema(implementation = CurrencyExchangeResponse.class),
        examples = {@ExampleObject(name = "EUR to PLN", value = "{\n" +
          "  \"from\": \"EUR\",\n" +
          "  \"to\": \"PLN\",\n" +
          "  \"exchange\": 4.57586842105263157895\n" +
          "}")})
    ),
    @ApiResponse(responseCode = "400", description = "Bad request",
      content = @Content(schema = @Schema(implementation = String.class))
    ),
    @ApiResponse(responseCode = "404", description = "Conversion not found",
      content = @Content(schema = @Schema(implementation = String.class))
    )
  })
  public ResponseEntity<CurrencyExchangeResponse> getExchange(
    @Parameter(name = "from", example = "EUR", description = "Set from currency")
    @RequestParam String from,
    @Parameter(name = "to", example = "PLN", description = "Set currency to exchange to")
    @RequestParam  String to,
    @Parameter(name = "date", example = "2022-04-05", description = "Set date in format yyyy-MM-dd or leave empty for latest date")
    @RequestParam(required = false)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
    from = from.toUpperCase(Locale.ROOT);
    to = to.toUpperCase(Locale.ROOT);
    validateCurrency(from);
    validateCurrency(to);
    log.info("getting exchange from:" + from + " to " + to + " for date " + date);
    CurrencyExchangeResponse crr = exchangeService.getExchangeRates(from, to, date);
    return ResponseEntity.ok(crr);
  }

  @PutMapping("/exchange")
  @Operation(summary = "Endpoint to update exchange")
  @ApiResponses({
    @ApiResponse(responseCode = "201", description = "Updated exchange successful",
      content = {
        @Content(
          mediaType = "application/json",
          array = @ArraySchema(
            schema = @Schema(implementation = Long.class)
          )
        )
      }
    )
  })
  public ResponseEntity<List<Long>> updateExchange() {
    log.info("update exchange");
    return ResponseEntity.status(201).body(exchangeService.saveOrUpdateExchange());
  }

  private void validateCurrency(String currency) {
    if(!CurrencyValidator.validCurrencies.contains(currency)){
      throw new BadCurrencyException();
    }
  }
}
