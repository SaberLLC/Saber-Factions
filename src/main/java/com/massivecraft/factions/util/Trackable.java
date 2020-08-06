package com.massivecraft.factions.util;

/**
 * Factions - Developed by Driftay.
 * All rights reserved 2020.
 * Creation Date: 8/6/2020
 */
public interface Trackable<T> {

    boolean track(T t);

    boolean untrack(T t);
}

