package com.core.common.services;

import java.util.concurrent.Callable;
import java.util.function.Supplier;

public interface RetryableService {

    <T extends Object> T execute(Callable<T> var1) throws Exception;
    <T> T runInRetryable(Supplier<T> supplier);
    <T> T runInRetryableNewTransaction(Supplier<T> supplier);
}
