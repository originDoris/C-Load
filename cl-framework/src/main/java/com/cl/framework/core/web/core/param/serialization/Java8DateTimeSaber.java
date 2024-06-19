package com.cl.framework.core.web.core.param.serialization;


import com.cl.framework.core.funcation.CubeFn;
import com.cl.framework.core.utils.DateUtil;
import com.cl.framework.core.utils.FieldUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;

/**
 * data
 * @author xhz
 */
public class Java8DateTimeSaber extends BaseFieldSaber {
    @Override
    public <T> Object from(final T input) {
        return CubeFn.getDefault(null, () -> {
            Object reference = null;
            if (input instanceof LocalDate) {
                final LocalDate date = (LocalDate) input;
                reference = date.toString();
            } else if (input instanceof LocalDateTime) {
                final LocalDateTime dateTime = (LocalDateTime) input;
                reference = dateTime.toString();
            } else if (input instanceof LocalTime) {
                final LocalTime time = (LocalTime) input;
                reference = time.toString();
            }
            return reference;
        }, input);
    }

    @Override
    public Object from(final Class<?> paramType, final String literal) {
        return CubeFn.getDefault(null,() ->
                        CubeFn.getSemi(Date.class == paramType ||
                                        Calendar.class == paramType, this.getLogger(),
                                () -> {
                                    this.verifyInput(!FieldUtil.isDate(literal), paramType, literal);
                                    final Date reference = DateUtil.parse(literal);
                                    if (LocalTime.class == paramType) {
                                        return DateUtil.toTime(literal);
                                    } else if (LocalDate.class == paramType) {
                                        return DateUtil.toDate(literal);
                                    } else if (LocalDateTime.class == paramType) {
                                        return DateUtil.toDateTime(literal);
                                    }
                                    return reference;
                                }, Date::new),
                paramType, literal);
    }
}
