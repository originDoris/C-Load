package com.cl.framework.core.annotation;

import com.cl.framework.core.enums.JobType;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * 扫描并注册定时任务
 * 1. FIXED 在固定时间戳执行
 * 2. PLAN 由调度器管理，从启动开始 周期执行
 * 3. ONCE 该任务可以由另一个job触发，也可以手动触发，只运行一次，存储在JobStore中
 * @author xhz
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface Job {

    JobType value();

    String name() default "";


    /**
     * 当前job的持续时间 默认
     * 30 s
     */
    long duration() default 300;

    long threshold() default 900;

    TimeUnit durationUnit() default TimeUnit.SECONDS;

    TimeUnit thresholdUnit() default TimeUnit.SECONDS;
}
