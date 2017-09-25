package com.github.leosilvadev.transactions.controllers;

import com.github.leosilvadev.transactions.domains.Transaction;
import com.github.leosilvadev.transactions.domains.TransactionsSummary;
import com.github.leosilvadev.transactions.services.TransactionManager;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * Created by leonardo on 9/25/17.
 */
@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionManager manager;

    public TransactionController(final TransactionManager manager) {
        this.manager = manager;
    }

    @GetMapping
    public TransactionsSummary getAll() {
        return this.manager.getSummary();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void register(@RequestBody @Valid final Transaction transaction) {
        this.manager.register(transaction);
    }

}
