package com.cl.framework.core.web.core.inquirer;

import java.util.Set;

/**
 * Package scanner
 *
 * @author xhz
 */
public interface Inquirer<R> {

    R scan(Set<Class<?>> classes);
}
