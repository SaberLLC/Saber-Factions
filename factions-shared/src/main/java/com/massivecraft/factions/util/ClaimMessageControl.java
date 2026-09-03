package com.massivecraft.factions.util;

import java.util.function.BooleanSupplier;

public final class ClaimMessageControl {

    private static final ThreadLocal<Boolean> SUPPRESS_SUCCESS_MESSAGES = ThreadLocal.withInitial(() -> false);

    private ClaimMessageControl() {
    }

    public static boolean shouldNotifySuccess() {
        return !SUPPRESS_SUCCESS_MESSAGES.get();
    }

    public static boolean withoutSuccessMessages(BooleanSupplier action) {
        boolean previous = SUPPRESS_SUCCESS_MESSAGES.get();
        SUPPRESS_SUCCESS_MESSAGES.set(true);
        try {
            return action.getAsBoolean();
        } finally {
            SUPPRESS_SUCCESS_MESSAGES.set(previous);
        }
    }
}
