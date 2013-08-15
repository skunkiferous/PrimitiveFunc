package com.blockwithme.fn.util;

/**
 * The Tuple interface extended by all the Tuple interfaces
 * that are generated GenTuple Generator
 *
 * @author sdiot
 */
public abstract class Tuple {
    /** Compares 2 objects. */
    protected static boolean equals(final Object a, final Object b) {
        return (a == null) ? (b == null) : a.equals(b);
    }

    /** Hashcode for boolean. */
    protected static int hash(final boolean value) {
        return value ? 123 : 456;
    }

    /** Hashcode for byte. */
    protected static int hash(final byte value) {
        return value;
    }

    /** Hashcode for short. */
    protected static int hash(final short value) {
        return value;
    }

    /** Hashcode for char. */
    protected static int hash(final char value) {
        return value;
    }

    /** Hashcode for int. */
    protected static int hash(final int value) {
        return value;
    }

    /** Hashcode for int. */
    protected static int hash(final long value) {
        return (int) (value ^ (value >>> 32));
    }

    /** Hashcode for float. */
    protected static int hash(final float value) {
        return Float.floatToIntBits(value);
    }

    /** Hashcode for double. */
    protected static int hash(final double value) {
        final long bits = Double.doubleToLongBits(value);
        return (int) (bits ^ (bits >>> 32));
    }

    /** Hashcode for Objects. */
    protected static int hash(final Object value) {
        return value == null ? 1 : value.hashCode();
    }

    /** Returns the number of fields */
    public final int size() {
        return getSignature().length;
    }

    /** Returns the type of the fields */
    public abstract Class<?>[] getSignature();

    /** Returns the field with the given number */
    public abstract Object get(final int fieldNumber);
}
