package com.cl.service;

import com.cl.repository.tables.pojos.ClCodeConfig;
import io.reactivex.rxjava3.core.Single;
import io.vertx.core.json.JsonObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 代码执行器
 *
 * @author xhz
 */
public interface ExecuteCodeService {

    Single<Object> execute(ClCodeConfig clCodeConfig, Map<String, Object> param);




}
