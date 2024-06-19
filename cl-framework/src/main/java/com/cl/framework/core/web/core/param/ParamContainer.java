package com.cl.framework.core.web.core.param;

import com.cl.framework.core.constants.ParamIdConstant;
import com.cl.framework.core.enums.ResolverType;
import lombok.Data;

import java.io.Serializable;
import java.lang.annotation.Annotation;

/**
 *
 * 参数容器
 * @author xhz
 */
@Data
public class ParamContainer<T> implements Serializable {

    private Object defaultValue;
    private String name;
    private ResolverType type;
    private Class<?> argType;
    private Annotation annotation;

    private T value;


    public void setName(String name) {
        if (ParamIdConstant.DIRECT.equals(name)) {
            this.type = ResolverType.RESOLVER;
        } else if (ParamIdConstant.IGNORE.equals(name)) {
            this.type = ResolverType.TYPED;
        } else {
            this.type = ResolverType.STANDARD;
        }
        this.name = name;
    }
}
