package com.cl.framework.core.enums;


/**
 * 消息模型 在@Worker 注解使用
 * @author xhz
 */
public enum MessageModel {
    REQUEST_RESPONSE,
    REQUEST_MICRO_WORKER,
    PUBLISH_SUBSCRIBE,
    DISCOVERY_PUBLISH,
    ONE_WAY
}
