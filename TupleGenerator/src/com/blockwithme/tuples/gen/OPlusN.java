/*
 * Copyright (C) 2013 Sebastien Diot.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.blockwithme.tuples.gen;

/**
 * Accepts tuple parameter types, up to N, and then Object, plus N.
 *
 * @author monster
 */
public class OPlusN implements TupleFilter {
    private final int n;

    /** Constructor */
    public OPlusN(final int theN) {
        n = theN;
    }

    /* (non-Javadoc)
     * @see com.blockwithme.tuples.gen.TupleFilter#accept(com.blockwithme.fn.gen.TupleFilter.ParamType[])
     */
    @Override
    public boolean accept(final ParamType[] paramTypes) {
        return (paramTypes.length < n)
                || ((paramTypes.length == n) && (paramTypes[0] == ParamType.Object));
    }
}
