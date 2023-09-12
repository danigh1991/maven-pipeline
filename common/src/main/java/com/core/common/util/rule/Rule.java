package com.core.common.util.rule;

import java.util.List;
import java.util.Set;

public interface Rule <T> {

    void validate(T target);

    default void validate(List<T> targets) {
        targets.forEach(this::validate);
    }

    default void validate(Set<T> targets) {
        targets.forEach(this::validate);
    }
}
