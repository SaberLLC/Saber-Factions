package com.massivecraft.factions.cmd.reserve;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * @author Saser
 */
public class ListParameterizedType implements ParameterizedType {
    private Type type;

    public ListParameterizedType(Type type) {
        this.type = type;
    }

    @Override
    public Type[] getActualTypeArguments() {
        return new Type[]{this.type};
    }

    @Override
    public Type getRawType() {
        return ArrayList.class;
    }

    @Override
    public Type getOwnerType() {
        return null;
    }
}
