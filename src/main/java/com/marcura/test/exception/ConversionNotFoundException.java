package com.marcura.test.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import static com.marcura.test.util.Constants.NOT_FOUND_CONVERSION;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = NOT_FOUND_CONVERSION)
public class ConversionNotFoundException extends RuntimeException {
}