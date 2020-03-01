package com.massivecraft.factions.util.Sync;

import com.massivecraft.factions.FactionsPlugin;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;

/**
 * @author droppinganvil
 */
public class SyncTask {
    private Object o;
    private Method m;
    private Object[] arguments;
    private Collection<Class<?>> params;

    public SyncTask(Object target, Method method, Object... arguments) {
        this.o = target;
        this.m = method;
        this.arguments = arguments;
    }

    /**
     * Generate sync task without the ability to specify the Method object, made for easy typing
     * @param target Object the method is on
     * @param method Method name
     * @param arguments Objects needed to call method
     */
    public SyncTask(Object target, String method, Object... arguments) {
        this.o = target;
        this.arguments = arguments;
        this.params = new ArrayList<>();
        for (Object o : this.arguments) {
            this.params.add(o.getClass());
        }
        try {
            this.m = o.getClass().getMethod(method, (Class<?>[]) params.toArray());
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            FactionsPlugin.getInstance().getLogger().log(Level.SEVERE, "A Method could not be located! This means a task that was supposed to happen did not, this may be a very serious issue");
        }

    }
    /**
     * Generate sync task without the ability to specify the Method object or arguments, made for easy typing
     */
    public SyncTask(Object target, String method) {
        this.o = target;
        try {
            this.m = o.getClass().getMethod(method);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            FactionsPlugin.getInstance().getLogger().log(Level.SEVERE, "A Method could not be located! This means a task that was supposed to happen did not, this may be a very serious issue");
        }
    }

    public void call() throws InvocationTargetException, IllegalAccessException {
        m.invoke(o, arguments);
    }
}
