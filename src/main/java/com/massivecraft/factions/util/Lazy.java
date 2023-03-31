/*
 * MIT License
 *
 * Copyright (c) 2023, Atilt
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.massivecraft.factions.util;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * A generic Lazy implementation.
 * Unlike standard {@link Supplier<L>}, this will cache the
 * result upon first request.
 *
 * @author Atilt
 *
 * @param <L> the type of object
 */
public final class Lazy<L> implements Supplier<L> {
    
    private final Supplier<L> handle;
    private L value;

    private Lazy(@Nonnull Supplier<L> handle) {
        Objects.requireNonNull(handle, "handle");
        this.handle = handle;
    }

    public static <L> Lazy<L> of(@Nonnull Supplier<L> handle) {
        Objects.requireNonNull(handle, "handle");
        return new Lazy<>(handle);
    }


    /**
     * The underlying supplier that will provide
     * the cached value.
     *
     * @since 1.0.0
     *
     * @return the original supplier
     */
    @Nonnull
    public Supplier<L> handle() {
        return this.handle;
    }

    /**
     * The lazy is set to be empty if the value of
     * the underlying supplier has not yet been cached.
     *
     * @since 1.0.0
     *
     * @return if the cached value is present
     */
    public boolean empty() {
        return this.value == null;
    }

    /**
     * Returns the cached value, or caches then returns
     * the value if not yet cached.
     *
     * @since 1.0.0
     *
     * @return the cached value
     */
    @Override
    public L get() {
        if (this.value == null) {
            this.value = this.handle.get();
        }
        return value;
    }
}