package com.core.common.transformer;

public interface Transformer <S, D> {
    D transform(S source, D destination);
}
