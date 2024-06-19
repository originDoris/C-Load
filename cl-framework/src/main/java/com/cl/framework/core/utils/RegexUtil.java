package com.cl.framework.core.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author xhz
 * RegexUtil
 */
public class RegexUtil {
    public static boolean regex(String regex,String value) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(value);
        // 执行匹配操作，并输出匹配结果
        return matcher.find();
    }
}
