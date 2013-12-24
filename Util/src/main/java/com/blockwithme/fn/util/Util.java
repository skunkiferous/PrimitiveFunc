package com.blockwithme.fn.util;

import static java.util.Objects.requireNonNull;

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

    /** The default class name "infix" for Functor interfaces */
    public static final String FUNC_CLASS_NAME_INFIX = "Func";

    /** The default class name prefix for "void" Functor interfaces */
    public static final String PROC_CLASS_NAME_PREFIX = "Proc";

    /** The default package name prefix for Functor interfaces. */
    public static final String FUNC_PACKAGE_NAME_PREFIX = "com.blockwithme.fn";

    /** The max number of classes (parameters of Tuple) . */
    private static final int TUPLE_MAX_CLASSES = FUNC_MAX_CLASSES - 1;

    /** The default class name prefix for Tuple classes */
    public static final String TUPLE_CLASS_NAME_PREFIX = "T";

    /** The default package name for Tuple classes. */
    public static final String TUPLE_PACKAGE_NAME = "com.blockwithme.tuples";

    /** The field name of the SIGNATURE constant. */
    private static final String SIGNATURE = "SIGNATURE";

    /** The default short Void.TYPE name label. */
    private static final String SHORT_VOID_LABEL = "P";

    /** The default short Boolean.TYPE name label. */
    private static final String SHORT_BOOLEAN_LABEL = "Z";

    /** The default short Byte.TYPE name label. */
    private static final String SHORT_BYTE_LABEL = "B";

    /** The default short Character.TYPE name label. */
    private static final String SHORT_CHAR_LABEL = "C";

    /** The default short Short.TYPE name label. */
    private static final String SHORT_SHORT_LABEL = "S";

    /** The default short Integer.TYPE name label. */
    private static final String SHORT_INT_LABEL = "I";

    /** The default short Long.TYPE name label. */
    private static final String SHORT_LONG_LABEL = "L";

    /** The default short Float.TYPE name label. */
    private static final String SHORT_FLOAT_LABEL = "F";

    /** The default short Double.TYPE name label. */
    private static final String SHORT_DOUBLE_LABEL = "D";

    /** The default short Object/Other name label. */
    private static final String SHORT_OBJECT_LABEL = "O";

    /** The default short type labels. */
    public static final String[] SHORT_LABELS = new String[] {
            SHORT_VOID_LABEL, SHORT_BOOLEAN_LABEL, SHORT_BYTE_LABEL,
            SHORT_CHAR_LABEL, SHORT_SHORT_LABEL, SHORT_INT_LABEL,
            SHORT_LONG_LABEL, SHORT_FLOAT_LABEL, SHORT_DOUBLE_LABEL,
            SHORT_OBJECT_LABEL };

    /** The default long Void.TYPE name label. */
    private static final String LONG_VOID_LABEL = "Void";

    /** The default long Boolean.TYPE name label. */
    private static final String LONG_BOOLEAN_LABEL = "Boolean";

    /** The default long Byte.TYPE name label. */
    private static final String LONG_BYTE_LABEL = "Byte";

    /** The default long Character.TYPE name label. */
    private static final String LONG_CHAR_LABEL = "Char";

    /** The default long Short.TYPE name label. */
    private static final String LONG_SHORT_LABEL = "Short";

    /** The default long Integer.TYPE name label. */
    private static final String LONG_INT_LABEL = "Int";

    /** The default long Long.TYPE name label. */
    private static final String LONG_LONG_LABEL = "Long";

    /** The default long Float.TYPE name label. */
    private static final String LONG_FLOAT_LABEL = "Float";

    /** The default long Double.TYPE name label. */
    private static final String LONG_DOUBLE_LABEL = "Double";

    /** The default long Object/Other name label. */
    private static final String LONG_OBJECT_LABEL = "Object";

    /** The default long type labels. */
    public static final String[] LONG_LABELS = new String[] { LONG_VOID_LABEL,
            LONG_BOOLEAN_LABEL, LONG_BYTE_LABEL, LONG_CHAR_LABEL,
            LONG_SHORT_LABEL, LONG_INT_LABEL, LONG_LONG_LABEL,
            LONG_FLOAT_LABEL, LONG_DOUBLE_LABEL, LONG_OBJECT_LABEL };

    /** The Functor class infix. */
    private final String funcClassInfix;

    /** The "void" Functor class prefix. */
    private final String procClassPrefix;

    /** The Functor package name prefix. */
    private final String funcPackageNamePrefix;

    /** The Tuple class suffix. */
    private final String tupleClassPrefix;

    /** The Tuple package name. */
    private final String tuplePackageName;

    /** The Void.TYPE name label. */
    private final String voidLabel;

    /** The Boolean.TYPE name label. */
    private final String booleanLable;

    /** The Byte.TYPE name label. */
    private final String byteLabel;

    /** The Character.TYPE name label. */
    private final String charLabel;

    /** The Short.TYPE name label. */
    private final String shortLabel;

    /** The Integer.TYPE name label. */
    private final String intLabel;

    /** The Long.TYPE name label. */
    private final String longLabel;

    /** The Float.TYPE name label. */
    private final String floatLabel;

    /** The Double.TYPE name label. */
    private final String doubleLabel;

    /** The Object/Other name label. */
    private final String objectLabel;

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
     * Instantiates the util with default values defined above,
     * using long labels.
     *
     * @see com.blockwithme.fn.gen.GenFunc.
     * */
    public Util() {
        this(false);
    }

    /**
     * Instantiates the util with default values defined above.
     *
     * @see com.blockwithme.fn.gen.GenFunc.
     * */
    public Util(final boolean shortLabels) {
        this(FUNC_PACKAGE_NAME_PREFIX,
                shortLabels ? SHORT_LABELS : LONG_LABELS,
                FUNC_CLASS_NAME_INFIX, PROC_CLASS_NAME_PREFIX,
                TUPLE_PACKAGE_NAME, TUPLE_CLASS_NAME_PREFIX);
    }

    /**
     * Instantiates a new util.
     *
     * @param theFuncPackageNamePrefix the package name of generated Functor interfaces.
     * @param theLabels the labels used to represent the possible parameter and return types.
     * @param theFuncClassInfix the class infix of Functors.
     * @param theProcClassPrefix the class prefix of "void" Functors.
     * @param theTuplePackageName the package name of generated Tuple classes.
     * @param theTupleClassPrefix the class prefix of Tuples.
     *
     * @see com.blockwithme.fn.gen.GenFunc.
     */
    public Util(final String theFuncPackageNamePrefix,
            final String[] theLabels, final String theFuncClassInfix,
            final String theProcClassPrefix, final String theTuplePackageName,
            final String theTupleClassPrefix) {
        funcPackageNamePrefix = requireNonNull(theFuncPackageNamePrefix,
                "theFuncPackageNamePrefix");
        funcClassInfix = requireNonNull(theFuncClassInfix, "theFuncClassInfix");
        procClassPrefix = requireNonNull(theProcClassPrefix,
                "theProcClassPrefix");
        tuplePackageName = requireNonNull(theTuplePackageName,
                "theTuplePackageName");
        tupleClassPrefix = requireNonNull(theTupleClassPrefix,
                "tupleClassPrefix");
        requireNonNull(theLabels, "theLabels");
        if (theLabels.length != SHORT_LABELS.length) {
            throw new IllegalArgumentException("theLabels.length must be "
                    + SHORT_LABELS.length);
        }
        for (final String s : theLabels) {
            requireNonNull(s, "theLabels[?]");
        }
        voidLabel = theLabels[0];
        booleanLable = theLabels[1];
        byteLabel = theLabels[2];
        charLabel = theLabels[3];
        shortLabel = theLabels[4];
        intLabel = theLabels[5];
        longLabel = theLabels[6];
        floatLabel = theLabels[7];
        doubleLabel = theLabels[8];
        objectLabel = theLabels[9];
    }

    /** Returns the "label" to use for the given type, in the interface/tuple name. */
    public String typeLabel(final Class<?> clzz) {
        if (clzz == Void.TYPE) {
            return voidLabel;
        } else if (clzz == Boolean.TYPE) {
            return booleanLable;
        } else if (clzz == Byte.TYPE) {
            return byteLabel;
        } else if (clzz == Character.TYPE) {
            return charLabel;
        } else if (clzz == Short.TYPE) {
            return shortLabel;
        } else if (clzz == Integer.TYPE) {
            return intLabel;
        } else if (clzz == Long.TYPE) {
            return longLabel;
        } else if (clzz == Float.TYPE) {
            return floatLabel;
        } else if (clzz == Double.TYPE) {
            return doubleLabel;
        } else {
            return objectLabel;
        }
    }

    /** Generates part of a name, based on the signature. */
    public String genNamePart(final Class<?>[] theSignature,
            final boolean skipFirst) {
        String className = "";
        for (int i = skipFirst ? 1 : 0; i < theSignature.length; i++) {
            className += typeLabel(theSignature[i]);
        }
        return className;
    }

    /** Generates a function/procedure name, based on the signature. */
    public String genFuncProcName(final Class<?>[] theSignature) {
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

        if (theSignature[0] == Void.TYPE) {
            return funcPackageNamePrefix + (theSignature.length - 1) + "."
                    + procClassPrefix + genNamePart(theSignature, true);
        }
        return funcPackageNamePrefix + (theSignature.length - 1) + "."
                + typeLabel(theSignature[0]) + funcClassInfix
                + genNamePart(theSignature, true);
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
        final String className = genFuncProcName(theSignature);

        try {
            return (Class<? extends Functor>) Class.forName(className);
        } catch (final ClassNotFoundException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    /** Generates a tuple name, based on the signature. */
    public String genTupleName(final Class<?>[] theSignature) {
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

        return tuplePackageName + '.' + tupleClassPrefix + theSignature.length
                + genNamePart(theSignature, false);
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
        final String className = genTupleName(theSignature);
        try {
            return (Functor) Class.forName(className).newInstance();
        } catch (final ClassNotFoundException | InstantiationException
                | IllegalAccessException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }
}
