package com.blockwithme.fn.test;

import com.blockwithme.fn.F1BB;
import com.blockwithme.fn.F2BBF;
import com.blockwithme.fn.util.Functor;
import com.blockwithme.fn.util.Util;

public class Test {

    private static void print(final Class<?>[] signature) {
        for (final Class<?> clsz : signature) {
            System.out.println(clsz.getName());
        }
    }

    public static void main(final String[] args) {

        final Functor f = new F1BB() {
            @Override
            public byte apply(final byte p0) {
                return 0;
            }
        };

        final Class<?>[] signature = Util.getSignature(f);
        System.out.println("-------------------");
        print(signature);
        System.out.println("-------------------");
        print(Util.getSignature(F1BB.class));
        final Util ut = new Util();
        System.out.println("-------------------");
        System.out.println(ut.getFunctor(signature).getName());
        System.out.println("-------------------");
        print(Util.getSignature(F2BBF.class));
        System.out.println("-------------------");
        System.out.println(ut.getFunctor(Util.getSignature(F2BBF.class))
                .getName());
        System.out.println("-------------------");

    }
}
