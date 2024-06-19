package com.cl.framework.core.enums;

/**
 * job type
 * @author xhz
 */

public enum JobType {
    // 定时执行
    FIXED,
    // 启动时执行 然后每隔一段时间执行
    PLAN,
    // 触发式的 执行完就停止
    ONCE,
    CONTAINER,
}
