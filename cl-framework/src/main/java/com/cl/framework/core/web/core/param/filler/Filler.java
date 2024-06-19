package com.cl.framework.core.web.core.param.filler;

import com.cl.framework.core.annotation.BodyParam;
import com.cl.framework.core.annotation.ContextParam;
import com.cl.framework.core.annotation.SessionParam;
import com.cl.framework.core.annotation.StreamParam;
import com.cl.framework.core.utils.reflection.ReflectionUtils;
import io.vertx.rxjava3.ext.web.RoutingContext;

import javax.ws.rs.*;
import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 *
 * 统一获取参数以支持JSR311
 * filler 的作用是将参数填充到容器中
 * @author xhz
 */
public interface Filler {


    @SuppressWarnings("all")
    ConcurrentMap<Class<? extends Annotation>, Filler> PARAMS =
            new ConcurrentHashMap<Class<? extends Annotation>, Filler>() {
                {
                    // JSR311
                    this.put(QueryParam.class, ReflectionUtils.singleton(QueryFiller.class));
                    this.put(FormParam.class, ReflectionUtils.singleton(FormFiller.class));
                    this.put(PathParam.class, ReflectionUtils.singleton(PathFiller.class));
                    this.put(HeaderParam.class, ReflectionUtils.singleton(HeaderFiller.class));
                    this.put(CookieParam.class, ReflectionUtils.singleton(CookieFiller.class));

                    this.put(BodyParam.class, ReflectionUtils.singleton(EmptyFiller.class));
                    this.put(StreamParam.class, ReflectionUtils.singleton(EmptyFiller.class));
                    this.put(SessionParam.class, ReflectionUtils.singleton(SessionFiller.class));
                    this.put(ContextParam.class, ReflectionUtils.singleton(ContextFiller.class));
                }
            };

    Set<Class<? extends Annotation>> NO_VALUE = new HashSet<Class<? extends Annotation>>() {
                {
                    this.add(BodyParam.class);
                    this.add(StreamParam.class);
                }
            };

    /**
     * 这里的主要代码逻辑是获取输入字段名称的值。
     */
    Object apply(String name, Class<?> paramType, RoutingContext datum);
}
