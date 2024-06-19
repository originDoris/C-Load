package com.cl.framework.core.utils;

import com.cl.framework.core.funcation.CubeFn;


import java.util.*;

/**
 * @author lang
 */
public class StringUtil {


    private StringUtil() {
    }




   public static String join(final Object[] input, final String separator) {
        final Set<String> hashSet = new HashSet<>();
        Arrays.stream(input).filter(Objects::nonNull)
                .map(Object::toString).forEach(hashSet::add);
        return join(hashSet, separator);
    }

    public  static String join(final Collection<String> input, final String separator) {
        final String connector = (null == separator) ? "," : separator;
        return CubeFn.getDefault(null, () -> {
            final StringBuilder builder = new StringBuilder();
            final int size = input.size();
            int start = 0;
            for (final String item : input) {
                builder.append(item);
                start++;
                if (start < size) {
                    builder.append(connector);
                }
            }
            return builder.toString();
        }, input);
    }

    private static String repeat(final Integer times, final char fill) {
        final StringBuilder builder = new StringBuilder();
        for (int idx = 0; idx < times; idx++) {
            builder.append(fill);
        }
        return builder.toString();
    }

    public static String adjust(final Integer seed, final Integer width, final char fill) {
        return adjust(seed.toString(), width, fill);
    }

    public static String adjust(final String seed, final Integer width, final char fill) {
        final StringBuilder builder = new StringBuilder();
        final int seedLen = seed.length();
        int fillLen = width - seedLen;
        if (0 > fillLen) {
            fillLen = 0;
        }
        builder.append(repeat(fillLen, fill));
        builder.append(seed);
        return builder.toString();
    }


}
