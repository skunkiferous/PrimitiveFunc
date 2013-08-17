package com.blockwithme.fn.util;

import java.lang.reflect.Field;

/**
 * Provides utility methods to retrieve Functor interfaces from a signature
 * and the signatures of a particular Functor interface.
 *
 * @see com.blockwithme.fn.gen.GenFunc
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
     * Gets the signature of a particular Functor interface.
     *
     * @param theFunctor the Type of Functor interface .
     * @return the signature of the Function in the Functor interface.
     * An array of types (classes), return type being as the first element
     * and the parameter types as subsequent elements.
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
     * Gets the signature of a particular Functor interface.
     *
     * @param theFunctor the Functor instance.
     * @return the signature of the Function in the Functor interface.
     * An array of types (classes), return type being as the first element
     * and the parameter types as subsequent elements.
     */
    public static Class<?>[] getSignature(final Functor theFunctor) {
        return getSignature(theFunctor.getClass());
    }

    /**
     * Instantiates the util with default values. The package name of generated
     * Functor interfaces defaults to "com.blockwithme.fn" and the class prefix
     * defaults to "F" when this constructor is used
     *
     * @see com.blockwithme.fn.gen.GenFunc.
     * */
    public Util() {
        this(PACKAGE_NAME, CLASS_NAME_PREFIX);
    }

    /**
     * Instantiates a new util.
     *
     * @param thePackageName the package name of generated Functor interfaces.
     * @param theClassPrefix the class prefix.
     *
     * @see com.blockwithme.fn.gen.GenFunc.
     */
    public Util(final String thePackageName, final String theClassPrefix) {
        if (thePackageName == null) {
            throw new IllegalArgumentException("thePackageName is null");
        }
        if (theClassPrefix == null) {
            throw new IllegalArgumentException("theClassPrefix is null");
        }
        packageName = thePackageName;
        classPrefix = theClassPrefix;
    }

    /**
     * Gets the functor for a given signature.
     *
     * @param signature the signature of the Function An array of types (classes),
     * where the return type being as the first element and the parameter types as subsequent elements.
     * Expects the primitive types (Byte.TYPE) instead of the wrapper types (Byte.class).
     *
     * @return the Type of Functor interface.
     */
    @SuppressWarnings("unchecked")
    public Class<? extends Functor> getFunctor(
            @SuppressWarnings("rawtypes") final Class... theSignature) {

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
