package com.cl.framework.core.funcation;

/**
 * 无返回数据的自定义函数
 *
 * @author xhz
 */
@FunctionalInterface
public interface Actuator {
    void execute() throws Exception;
}
