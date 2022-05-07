package com.marcura.test.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import static com.marcura.test.util.Constants.BAD_CURRENCY;
import static com.marcura.test.util.Constants.NOT_FOUND_CONVERSION;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = BAD_CURRENCY)
public class BadCurrencyException extends RuntimeException {
}