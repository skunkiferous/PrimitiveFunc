package com.blockwithme.fn.util;

import java.io.Serializable;

/**
 * <code>TupleBase</code> is extended by all the Tuple instances
 * that are generated GenTuple Generator
 *
 * @author sdiot
 */
public abstract class TupleBase implements Tuple, Serializable {

    /** serialVersionUID */
    private static final long serialVersionUID = 1L;

    /** Cached hashcode. */
    protected transient int hashCode;

    /** Cached toString. */
    protected transient String toString;

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

    /* (non-Javadoc)
     * @see com.blockwithme.fn.util.Tuple#size()
     */
    @Override
    public final int size() {
        return getSignature().length;
    }

    /* (non-Javadoc)
     * @see com.blockwithme.fn.util.Tuple#toArray()
     */
    @Override
    public final Object[] toArray() {
        final Object[] result = new Object[size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = get(i);
        }
        return result;
    }

    /* (non-Javadoc)
     * @see com.blockwithme.fn.util.Tuple#getSignature()
     */
    @Override
    public abstract Class<?>[] getSignature();

    /* (non-Javadoc)
     * @see com.blockwithme.fn.util.Tuple#get(int)
     */
    @Override
    public abstract Object get(final int fieldNumber);
}
