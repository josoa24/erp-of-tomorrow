package com.hackaton.stock.common.exception;

public class InsufficientStockException extends RuntimeException {

    public InsufficientStockException(String product, int available, int requested) {
        super("Stock insuffisant pour " + product + " : disponible " + available + ", demandé " + requested);
    }
}
