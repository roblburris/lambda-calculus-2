package org.roblburris.lambda.calculus.util;

import com.google.common.collect.ImmutableList;

import java.util.List;

public final class Utils {
    private Utils() {}

    public static <T> List<T> prepend(List<T> l, T t) {
        return ImmutableList.<T>builder()
                .add(t)
                .addAll(l)
                .build();
    }
}
