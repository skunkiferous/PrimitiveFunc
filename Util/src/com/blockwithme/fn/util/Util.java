/**
 *
 */
package com.blockwithme.fn.util;

import java.lang.reflect.Field;

/**
 *
 * @author tarung
 *
 */
public class Util {

    public static Class<?>[] getSignature(
            final Class<? extends Functor> theClass) {
        try {
            final Field f = theClass.getField("SIGNATURE");
            return (Class[]) f.get(null);
        } catch (NoSuchFieldException | SecurityException
                | IllegalArgumentException | IllegalAccessException e) {
            throw new IllegalStateException(e.getMessage());
        }
    }

    public static Class<?>[] getSignature(final Functor theFunctor) {
        return getSignature(theFunctor.getClass());
    }

    public Class<? extends Functor> getFunctor(final Class... signature) {
        // TODO implementation
        return null;
    }
}
