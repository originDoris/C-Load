package com.cl.framework.core.web.core.inquirer;

import com.cl.framework.core.annotation.Worker;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * 扫描Worker注解
 *
 * @author xhz
 */
public class WorkerInquirer implements Inquirer<Set<Class<?>>>{
    @Override
    public Set<Class<?>> scan(Set<Class<?>> classes) {
        return classes.stream()
                .filter((item) -> item.isAnnotationPresent(Worker.class))
                .collect(Collectors.toSet());
    }
}
