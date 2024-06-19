package com.cl.framework.core.runtime.pkg;

import com.cl.framework.core.constants.MessageConstant;
import lombok.extern.slf4j.Slf4j;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

/**
 * 包扫描
 *
 * @author xhz
 */
@Slf4j
public class PackHunter {



    public static Set<String> getPackages(String[] filters){
        final Set<String> packageDirs = new TreeSet<>();
        final Package[] packages = Package.getPackages();
        for (final Package pkg : packages) {
            final String pending = pkg.getName();
            final boolean skip = Arrays.stream(filters).anyMatch(pending::startsWith);
            if (skip) {
                packageDirs.add(pending);
            }
        }
        log.info(MessageFormat.format(MessageConstant.PACKAGES, packageDirs.size(), packages.length));
        return packageDirs;
    }

}
