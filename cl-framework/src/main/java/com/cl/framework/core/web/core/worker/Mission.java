package com.cl.framework.core.web.core.worker;

import com.cl.framework.core.annotation.Off;
import com.cl.framework.core.annotation.On;
import com.cl.framework.core.constants.ErrorInfoConstant;
import com.cl.framework.core.constants.MessageConstant;
import com.cl.framework.core.enums.ErrorCodeEnum;
import com.cl.framework.core.enums.JobStatus;
import com.cl.framework.core.enums.JobType;
import com.cl.framework.core.exception.DataTableException;
import com.cl.framework.core.funcation.CubeFn;
import com.cl.framework.core.utils.StringUtil;
import com.cl.framework.core.utils.jackson.ClassDeserializer;
import com.cl.framework.core.utils.jackson.ClassSerializer;
import com.cl.framework.core.utils.jackson.JsonObjectDeserializer;
import com.cl.framework.core.utils.jackson.JsonObjectSerializer;
import com.cl.framework.core.utils.reflection.ReflectionUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.time.Instant;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * 用来存储job的详细信息
 * 扫描每个@Job的类和每个JobStore接口
 * @author xhz
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Mission implements Serializable {
    private static final Logger LOGGER = LoggerFactory.getLogger(Mission.class);

    private JobStatus status = JobStatus.STARTING;

    private String name;

    private JobType type;

    private String code;

    private String comment;

    private boolean readOnly;
    @JsonSerialize(using = JsonObjectSerializer.class)
    @JsonDeserialize(using = JsonObjectDeserializer.class)
    private JsonObject metadata = new JsonObject();

    @JsonSerialize(using = JsonObjectSerializer.class)
    @JsonDeserialize(using = JsonObjectDeserializer.class)
    private JsonObject additional = new JsonObject();

    @JsonIgnore
    private Instant instant = Instant.now();

    private long duration = -1;

    private long threshold = -1;

    @JsonSerialize(using = ClassSerializer.class)
    @JsonDeserialize(using = ClassDeserializer.class)
    private Class<?> income;

    private String incomeAddress;

    @JsonSerialize(using = ClassSerializer.class)
    @JsonDeserialize(using = ClassDeserializer.class)
    private Class<?> outcome;

    private String outcomeAddress;

    @JsonIgnore
    private Object proxy;

    @JsonIgnore
    private Method on;

    @JsonIgnore
    private Method off;

    public Mission connect(final Class<?> clazz) {

        this.proxy = ReflectionUtils.singleton(clazz);
        this.on = Arrays.stream(clazz.getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(On.class))
                .findFirst().orElse(null);

        CubeFn.outError(LOGGER, null == this.on, DataTableException.class,
                ErrorCodeEnum.JOB_ON_MISSING_ERROR, MessageFormat.format(ErrorInfoConstant.JOB_ON_MISSING_ERROR, clazz.getName()));


        final Annotation on = this.on.getAnnotation(On.class);
        this.incomeAddress = this.invoke(on, "address", this::getIncomeAddress);
        this.income = this.invoke(on, "income", this::getIncome);
        if (StringUtils.isBlank(this.incomeAddress)) {
            this.incomeAddress = null;
        }

        this.off = Arrays.stream(clazz.getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(Off.class))
                .findFirst().orElse(null);
        if (Objects.nonNull(this.off)) {

            final Annotation out = this.off.getAnnotation(Off.class);
            this.outcomeAddress = this.invoke(out, "address", this::getOutcomeAddress);
            this.outcome = this.invoke(out, "outcome", this::getOutcome);
            if (StringUtils.isBlank(this.outcomeAddress)) {
                this.outcomeAddress = null;
            }
        } else {
            LOGGER.info(MessageFormat.format(MessageConstant.JOB_NO_OFF, this.getCode()));
        }

        return this;
    }

    private <T> T invoke(final Annotation annotation, final String annotationMethod,
                         final Supplier<T> supplier) {
        T reference = supplier.get();
        if (Objects.isNull(reference)) {
            reference = ReflectionUtils.invoke(annotation, annotationMethod);
        }
        return reference;
    }
}
