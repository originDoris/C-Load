package com.cl.framework.core.exception;

import com.cl.framework.core.constants.ErrorInfoConstant;

import java.text.MessageFormat;

/**
 * 表达式解析器
 *
 * @author xhz
 */
public class EmptyStreamException extends DataTableException {

    public EmptyStreamException(final String filename) {
        super(MessageFormat.format(ErrorInfoConstant.NIL_MSG, filename, null));
    }

    public EmptyStreamException(final String filename, final Throwable ex) {
        super(MessageFormat.format(ErrorInfoConstant.NIL_MSG, filename, ex.getCause()));
    }
}
