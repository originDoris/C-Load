package com.cl.framework.core.web.core.param.serialization;

import com.cl.framework.core.funcation.CubeFn;
import com.cl.framework.core.utils.DateUtil;
import com.cl.framework.core.utils.FieldUtil;

import java.util.Calendar;
import java.util.Date;

/**
 * Date, Calendar
 * @author xhz
 */
public class DateSaber extends BaseFieldSaber {

    @Override
    public Object from(final Class<?> paramType,
                       final String literal) {
        return CubeFn.getDefault(null, () ->
                        CubeFn.getSemi(Date.class == paramType || Calendar.class == paramType, getLogger(),
                                () -> {
                                    verifyInput(!FieldUtil.isDate(literal), paramType, literal);
                                    final Date reference = DateUtil.parse(literal);
                                    if (Calendar.class == paramType) {
                                        // Specific date format
                                        final Calendar calendar = Calendar.getInstance();
                                        calendar.setTime(reference);
                                        return calendar;
                                    }
                                    return reference;
                                }, Date::new),
                paramType, literal);
    }

    @Override
    public <T> Object from(final T input) {
        return CubeFn.getDefault(null, () -> {
            Object reference = null;
            if (input instanceof Date) {
                final Date date = (Date) input;
                reference = date.getTime();
            } else if (input instanceof Calendar) {
                final Calendar date = (Calendar) input;
                reference = date.getTime().getTime();
            }
            return reference;
        }, input);
    }
}
