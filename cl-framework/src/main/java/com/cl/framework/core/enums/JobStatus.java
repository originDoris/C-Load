package com.cl.framework.core.enums;

/**
 * 任务运行状态
 * STARTING ------|
 *                v
 *     |------> READY <-------------------|
 *     |          |                       |
 *     |          |                     <start>
 *     |          |                       |
 *     |        <start>                   |
 *     |          |                       |
 *     |          V                       |
 *     |        RUNNING --- <stop> ---> STOPPED
 *     |          |
 *     |          |
 * <resume>   ( error )
 *     |          |
 *     |          |
 *     |          v
 *     |------- ERROR
 * @author xhz
 */
public enum JobStatus {
    STARTING,
    READY,
    RUNNING,
    STOPPED,
    ERROR,
}
