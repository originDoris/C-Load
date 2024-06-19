package com.cl.framework.plugin.jooq.util.condition;

import com.cl.framework.core.utils.reflection.ReflectionUtils;
import com.cl.framework.plugin.jooq.util.condition.date.*;
import com.cl.framework.plugin.jooq.util.query.Inquiry;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


interface Pool {

    ConcurrentMap<Class<?>, Clause> CLAUSE_MAP = new ConcurrentHashMap<Class<?>, Clause>() {
        {
            this.put(Object.class, ReflectionUtils.newInstance(ClauseString.class));
            this.put(Boolean.class, ReflectionUtils.newInstance(ClauseBoolean.class));
            this.put(LocalDateTime.class, ReflectionUtils.newInstance(ClauseInstant.class));
            this.put(LocalDate.class, ReflectionUtils.newInstance(ClauseInstant.class));
            this.put(LocalTime.class, ReflectionUtils.newInstance(ClauseInstant.class));
            this.put(Number.class, ReflectionUtils.newInstance(ClauseNumber.class));
            this.put(Long.class, ReflectionUtils.newInstance(ClauseNumber.class));
            this.put(Integer.class, ReflectionUtils.newInstance(ClauseNumber.class));
            this.put(Short.class, ReflectionUtils.newInstance(ClauseNumber.class));
        }
    };

    ConcurrentMap<String, Term> TERM_OBJECT_MAP = new ConcurrentHashMap<String, Term>() {
        {
            this.put(Inquiry.Op.LT, ReflectionUtils.newInstance(TermLt.class));
            this.put(Inquiry.Op.LE, ReflectionUtils.newInstance(TermLe.class));
            this.put(Inquiry.Op.GT, ReflectionUtils.newInstance(TermGt.class));
            this.put(Inquiry.Op.GE, ReflectionUtils.newInstance(TermGe.class));
            this.put(Inquiry.Op.EQ, ReflectionUtils.newInstance(TermEq.class));
            this.put(Inquiry.Op.NEQ, ReflectionUtils.newInstance(TermNeq.class));
            this.put(Inquiry.Op.NULL, ReflectionUtils.newInstance(TermNull.class));
            this.put(Inquiry.Op.NOT_NULL, ReflectionUtils.newInstance(TermNotNull.class));
            this.put(Inquiry.Op.TRUE, ReflectionUtils.newInstance(TermTrue.class));
            this.put(Inquiry.Op.FALSE, ReflectionUtils.newInstance(TermFalse.class));
            this.put(Inquiry.Op.IN, ReflectionUtils.newInstance(TermIn.class));
            this.put(Inquiry.Op.NOT_IN, ReflectionUtils.newInstance(TermNotIn.class));
            this.put(Inquiry.Op.START, ReflectionUtils.newInstance(TermStart.class));
            this.put(Inquiry.Op.END, ReflectionUtils.newInstance(TermEnd.class));
            this.put(Inquiry.Op.CONTAIN, ReflectionUtils.newInstance(TermContain.class));
            this.put(Inquiry.Op.BETWEEN, ReflectionUtils.newInstance(TermBetween.class));
            this.put(Inquiry.Op.LIKE, ReflectionUtils.newInstance(TermLike.class));
            this.put(Inquiry.Op.NOT_LIKE, ReflectionUtils.newInstance(TermNotLike.class));
        }
    };

    ConcurrentMap<String, Term> TERM_DATE_MAP = new ConcurrentHashMap<String, Term>() {
        {
            this.put(Inquiry.Op.LT, ReflectionUtils.newInstance(TermDLt.class));
            this.put(Inquiry.Op.LE, ReflectionUtils.newInstance(TermDLe.class));
            this.put(Inquiry.Op.GT, ReflectionUtils.newInstance(TermDGt.class));
            this.put(Inquiry.Op.GE, ReflectionUtils.newInstance(TermDGe.class));
            this.put(Inquiry.Op.EQ, ReflectionUtils.newInstance(TermDEq.class));
        }
    };
}