/**
 *
 */
package com.blockwithme.fn.util;

import java.lang.reflect.Field;

// TODO: Auto-generated Javadoc
/**
 * Provides utility methods to retrieve Functor interfaces from a signature
 * and the signatures of a particular Functor interface.
 *
 * @author tarung
 *
 */
public class Util {

    /** The default class name prefix for Functor interfaces */
    private static final String CLASS_NAME_PREFIX = "F";

    /** The max number of classes (return type and parameters of Functor) . */
    private static final int MAX_CLASSES = 6;

    /** The default package name for Functor interfaces. */
    private static final String PACKAGE_NAME = "com.blockwithme.fn";

    /** The field name of the SIGNATURE constant. */
    private static final String SIGNATURE = "SIGNATURE";

    /** The class suffix. */
    private final String classPrefix;

    /** The package name. */
    private final String packageName;

    /**
     * Gets the signature of a particular Functor interface class.
     *
     * @param theFunctor the Functor interface class instance
     * @return the signature of the Function in a particular Functor interface.
     * An array of classes, the function return type being as the first element.
     */
    public static Class<?>[] getSignature(
            final Class<? extends Functor> theFunctor) {

        try {
            final Field f = theFunctor.getField(SIGNATURE);
            return (Class[]) f.get(null);
        } catch (NoSuchFieldException | SecurityException
                | IllegalArgumentException | IllegalAccessException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    /**
     * Gets the signature.
     *
     * @param theFunctor the functor
     * @return the signature
     */
    public static Class<?>[] getSignature(final Functor theFunctor) {
        return getSignature(theFunctor.getClass());
    }

    /**
     * Instantiates a new util.
     */
    public Util() {
        this(PACKAGE_NAME, CLASS_NAME_PREFIX);
    }

    /**
     * Instantiates a new util.
     *
     * @param thePackageName the package name
     * @param theClassPrefix the class prefix
     */
    public Util(final String thePackageName, final String theClassPrefix) {
        packageName = thePackageName;
        classPrefix = theClassPrefix;
    }

    /**
     * Gets the functor.
     *
     * @param signature the signature
     * @return the functor
     */
    public Class<? extends Functor> getFunctor(final Class... theSignature) {

        if (theSignature == null) {
            throw new IllegalArgumentException("theSignature is null");
        }
        if (theSignature.length < 1) {
            throw new IllegalArgumentException("theSignature is empty");
        }
        if (theSignature.length > MAX_CLASSES) {
            throw new IllegalArgumentException(
                    "Signature can have only maximum " + MAX_CLASSES
                            + " classes");
        }

        String className = packageName + "." + classPrefix
                + (theSignature.length - 1);

        for (final Class<?> clzz : theSignature) {
            if (clzz == Void.TYPE) {
                className += "P";
            } else if (clzz == Boolean.TYPE) {
                className += "Z";
            } else if (clzz == Byte.TYPE) {
                className += "B";
            } else if (clzz == Character.TYPE) {
                className += "C";
            } else if (clzz == Short.TYPE) {
                className += "S";
            } else if (clzz == Integer.TYPE) {
                className += "I";
            } else if (clzz == Long.TYPE) {
                className += "L";
            } else if (clzz == Float.TYPE) {
                className += "F";
            } else if (clzz == Double.TYPE) {
                className += "D";
            } else {
                className += "O";
            }
        }
        try {
            return (Class<? extends Functor>) Class.forName(className);
        } catch (final ClassNotFoundException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }
}
