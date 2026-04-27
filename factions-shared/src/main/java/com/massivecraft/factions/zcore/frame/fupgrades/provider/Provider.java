package com.massivecraft.factions.zcore.frame.fupgrades.provider;

import javax.annotation.Nonnull;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public interface Provider<T> extends Supplier<T> {

    T provide();

    @Override
    default T get() {
        return provide();
    }

    default Provider<T> combine(@Nonnull BiFunction<? super T, ? super T, ? extends T> combiner, @Nonnull Provider<? extends T> other) {
        return new Provider<T>() {
            @Override
            public T provide() {
                T provided = Provider.this.provide();
                T then = other.provide();
                return (provided != null && then != null) ? combiner.apply(provided, then) : null;
            }

            @Override
            public T get() {
                return provide();
            }
        };
    }
}