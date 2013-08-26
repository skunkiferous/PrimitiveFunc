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

    /** The max number of classes (return type and parameters of Functor) . */
    private static final int FUNC_MAX_CLASSES = 6;

    /** The default class name prefix for Functor interfaces */
    public static final String FUNC_CLASS_NAME_PREFIX = "F";

    /** The default package name for Functor interfaces. */
    public static final String FUNC_PACKAGE_NAME = "com.blockwithme.fn";

    /** The max number of classes (parameters of Tuple) . */
    private static final int TUPLE_MAX_CLASSES = FUNC_MAX_CLASSES - 1;

    /** The default class name prefix for Tuple classes */
    public static final String TUPLE_CLASS_NAME_PREFIX = "T";

    /** The default package name for Tuple classes. */
    public static final String TUPLE_PACKAGE_NAME = "com.blockwithme.tuples";

    /** The field name of the SIGNATURE constant. */
    private static final String SIGNATURE = "SIGNATURE";

    /** The Functor class suffix. */
    private final String funcClassPrefix;

    /** The Functor package name. */
    private final String funcPackageName;

    /** The Tuple class suffix. */
    private final String tupleClassPrefix;

    /** The Tuple package name. */
    private final String tuplePackageName;

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
        this(FUNC_PACKAGE_NAME, FUNC_CLASS_NAME_PREFIX, TUPLE_PACKAGE_NAME,
                TUPLE_CLASS_NAME_PREFIX);
    }

    /**
     * Instantiates a new util.
     *
     * @param theFuncPackageName the package name of generated Functor interfaces.
     * @param theFuncClassPrefix the class prefix of Functors.
     * @param theTuplePackageName the package name of generated Tuple classes.
     * @param theTupleClassPrefix the class prefix of Tuples.
     *
     * @see com.blockwithme.fn.gen.GenFunc.
     */
    public Util(final String theFuncPackageName,
            final String theFuncClassPrefix, final String theTuplePackageName,
            final String theTupleClassPrefix) {
        if (theFuncPackageName == null) {
            throw new IllegalArgumentException("theFuncPackageName is null");
        }
        if (theFuncClassPrefix == null) {
            throw new IllegalArgumentException("theFuncClassPrefix is null");
        }
        if (theTuplePackageName == null) {
            throw new IllegalArgumentException("theTuplePackageName is null");
        }
        if (theTupleClassPrefix == null) {
            throw new IllegalArgumentException("theTupleClassPrefix is null");
        }
        funcPackageName = theFuncPackageName;
        funcClassPrefix = theFuncClassPrefix;
        tuplePackageName = theTuplePackageName;
        tupleClassPrefix = theTupleClassPrefix;
    }

    private String genNamePart(final Class<?>[] theSignature) {
        String className = "";

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

        return className;
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
        if (theSignature.length > FUNC_MAX_CLASSES) {
            throw new IllegalArgumentException(
                    "Signature can have only maximum " + FUNC_MAX_CLASSES
                            + " classes");
        }

        final String className = funcPackageName + "." + funcClassPrefix
                + (theSignature.length - 1) + genNamePart(theSignature);

        try {
            return (Class<? extends Functor>) Class.forName(className);
        } catch (final ClassNotFoundException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    /**
     * Gets the Tuple-factory functor for a given signature.
     *
     * @param signature the signature of the Tuple An array of types (classes).
     * Expects the primitive types (Byte.TYPE) instead of the wrapper types (Byte.class).
     *
     * @return the Type of Functor interface.
     */
    public Functor getTupleFactoryFunctor(
            @SuppressWarnings("rawtypes") final Class... theSignature) {

        if (theSignature == null) {
            throw new IllegalArgumentException("theSignature is null");
        }
        if (theSignature.length < 1) {
            throw new IllegalArgumentException("theSignature is empty");
        }
        if (theSignature.length > TUPLE_MAX_CLASSES) {
            throw new IllegalArgumentException(
                    "Signature can have only maximum " + (FUNC_MAX_CLASSES - 1)
                            + " classes");
        }

        final String className = tuplePackageName + '.' + tupleClassPrefix
                + theSignature.length + genNamePart(theSignature);

        try {
            return (Functor) Class.forName(className).newInstance();
        } catch (final ClassNotFoundException | InstantiationException
                | IllegalAccessException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }
}
