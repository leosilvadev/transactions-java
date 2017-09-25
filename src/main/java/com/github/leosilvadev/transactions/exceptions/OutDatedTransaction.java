package com.github.leosilvadev.transactions.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by leonardo on 9/25/17.
 */
@ResponseStatus(HttpStatus.NO_CONTENT)
public class OutDatedTransaction extends RuntimeException {
}
