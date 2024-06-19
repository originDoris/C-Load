package com.cl.framework.core.web.core.param.serialization;


import com.cl.framework.core.constants.ErrorInfoConstant;
import com.cl.framework.core.enums.ErrorCodeEnum;
import com.cl.framework.core.exception.DataTableException;
import com.cl.framework.core.funcation.CubeFn;

import java.io.File;
import java.text.MessageFormat;

/**
 * file
 * @author xhz
 */
public class FileSaber extends BaseFieldSaber {

    @Override
    public Object from(final Class<?> paramType,
                       final String filename) {
        return CubeFn.getDefault(null, () -> {
            final File file = new File(filename);
            CubeFn.outError(getLogger(), !file.exists() || !file.canRead(), DataTableException.class, ErrorCodeEnum.FILE_NOT_EXIST_ERROR, MessageFormat.format(ErrorInfoConstant.FILE_NOT_EXIST_ERROR, filename, paramType));
            return file;
        }, paramType, filename);
    }
}
