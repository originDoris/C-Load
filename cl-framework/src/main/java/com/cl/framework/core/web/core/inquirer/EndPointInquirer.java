package com.cl.framework.core.web.core.inquirer;

import com.cl.framework.core.annotation.EndPoint;
import com.cl.framework.core.constants.MessageConstant;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * EndPoint 注解扫描
 *
 * @author xhz
 */
@Slf4j
public class EndPointInquirer implements Inquirer<Set<Class<?>>>{

    @Override
    public Set<Class<?>> scan(Set<Class<?>> classes) {
        final Set<Class<?>> endpoints = classes.stream()
                .filter((item) -> item.isAnnotationPresent(EndPoint.class))
                .collect(Collectors.toSet());
        log.info(MessageConstant.SCAN_ENDPOINT, endpoints.size());
        return endpoints;
    }
}
