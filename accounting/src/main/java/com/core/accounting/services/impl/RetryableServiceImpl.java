package com.core.accounting.services.impl;

import com.core.common.services.RetryableService;
import com.core.common.services.impl.AbstractService;
import org.hibernate.StaleObjectStateException;
import org.hibernate.exception.GenericJDBCException;
import org.hibernate.exception.LockAcquisitionException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.Callable;
import java.util.function.Supplier;

@Service("RetryableServiceImpl")
public class RetryableServiceImpl extends AbstractService implements RetryableService  {

    @Override
/*    @Retryable(
            value = {StaleObjectStateException.class, LockAcquisitionException.class, GenericJDBCException.class},
            maxAttempts = 4, backoff = @Backoff(delay=1500))*/
    public <T> T execute(Callable<T> cl) throws Exception {

            return cl.call();

    }

    @Override
/*    @Retryable(
            value = {StaleObjectStateException.class, LockAcquisitionException.class, GenericJDBCException.class},
            maxAttempts = 4, backoff = @Backoff(delay=1500))*/
    @Transactional
    public <T> T runInRetryable(Supplier<T> supplier) {
        return supplier.get();
    }


/*    @Retryable(
            value = {StaleObjectStateException.class, LockAcquisitionException.class, GenericJDBCException.class},
            maxAttempts = 4, backoff = @Backoff(delay=1500))*/
    @Transactional( propagation = Propagation.REQUIRES_NEW)
    public <T> T runInRetryableNewTransaction(Supplier<T> supplier) {
        return supplier.get();
    }

}
