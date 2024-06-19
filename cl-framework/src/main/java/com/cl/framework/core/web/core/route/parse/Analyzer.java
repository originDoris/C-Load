package com.cl.framework.core.web.core.route.parse;


import com.cl.framework.core.exception.WebException;
import com.cl.framework.core.runtime.Envelop;
import com.cl.framework.core.web.core.agent.Event;

/**
 * web请求的Mime解析
 * @author xhz
 */
public interface Analyzer extends Income<Object[]> {
    Envelop out(Envelop envelop, Event event) throws WebException;
}
