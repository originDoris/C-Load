package com.cl.service.impl;

import com.cl.context.ExecuteContext;
import com.cl.enums.CodeStatus;
import com.cl.framework.core.domain.BasePage;
import com.cl.framework.core.enums.ErrorCodeEnum;
import com.cl.framework.core.exception.ParamException;
import com.cl.framework.core.funcation.CubeFn;
import com.cl.framework.core.id.SnowFlakeIdWorker;
import com.cl.framework.core.utils.AsyncUtils;
import com.cl.framework.core.utils.DateUtil;
import com.cl.framework.core.vertx.VertxLauncher;
import com.cl.framework.plugin.jooq.JooqInfix;
import com.cl.framework.plugin.jooq.util.JooqReader;
import com.cl.framework.plugin.jooq.util.JooqWriter;
import com.cl.framework.plugin.jooq.util.JqAnalyzer;
import com.cl.framework.plugin.jooq.util.condition.JooqCond;
import com.cl.model.query.CodeConfigQuery;
import com.cl.repository.tables.pojos.ClCodeConfig;
import com.cl.service.CodeConfigService;
import com.cl.service.ExecuteCodeService;
import com.cl.util.UserUtils;
import io.reactivex.rxjava3.core.Single;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.jooq.Condition;

import javax.inject.Inject;
import java.text.MessageFormat;
import java.util.Map;

/**
 * CodeConfigServiceImpl
 *
 * @author xhz
 */
@Slf4j
public class CodeConfigServiceImpl implements CodeConfigService {

    @Inject
    private ExecuteContext executorContext;

    @Override
    public Single<Boolean> save(ClCodeConfig clCodeConfig) {
        return Single.defer(() -> {
            clCodeConfig.setId(SnowFlakeIdWorker.getInstance().nextId());
            clCodeConfig.setGmtCreate(DateUtil.getNow());
            clCodeConfig.setCreator(UserUtils.getUserId());
            return getDao().queryExecutor().beginTransaction()
                    .flatMap(transaction -> AsyncUtils.transactionClose(transaction.execute(dslContext -> JooqWriter.insert(dslContext, getDao().getTable(), clCodeConfig)), transaction, integer -> integer == 1));
        });
    }

    @Override
    public Single<Boolean> remove(Long id) {
        return Single.defer(() -> {
            return getDao().queryExecutor().beginTransaction()
                    .flatMap(transaction -> AsyncUtils.transactionClose(transaction.execute(dslContext -> {
                        JsonObject entries = new JsonObject();
                        entries.put("id", id);
                        JqAnalyzer jqAnalyzer = JqAnalyzer.get(getDao());
                        Condition transform = JooqCond.transform(entries, jqAnalyzer::column, null);
                        return dslContext.update(getDao().getTable()).set(jqAnalyzer.column("is_delete"), 1).where(transform);
                    }), transaction, integer -> integer == 1));
        });
    }

    @Override
    public Single<BasePage<ClCodeConfig>> query(CodeConfigQuery query) {
        return Single.defer(() -> {
            return JooqReader.searchPagination(getDao().getTable(), JooqInfix.getDSL(), query.getInquiry(), s -> JqAnalyzer.get(getDao()).column(s), record -> record.into(ClCodeConfig.class));
        });

    }

    @Override
    public Single<Boolean> publish(Long id) {
        return Single.defer(() -> {
            return getDao().findOneById(id).map(optional -> {
                if (optional.isEmpty()) {
                    throw new ParamException(ErrorCodeEnum.PARAM_ERROR_CODE, "代码配置不存在！");
                }
                ClCodeConfig clCodeConfig = optional.get();
                if (CodeStatus.PUBLISH.getCode().equals(clCodeConfig.getStatus())) {
                    throw new ParamException(ErrorCodeEnum.PARAM_ERROR_CODE, "代码已发布！");
                }
                return clCodeConfig;
            }).flatMap(clCodeConfig -> {

                VertxLauncher.getVertx().eventBus().<JsonObject>consumer(clCodeConfig.getServerAddress(), message -> {
                    JsonObject body = message.body();
                    executorContext.getExecuteService(clCodeConfig.getDsl()).flatMap(optional -> {
                        if (optional.isEmpty()) {
                            return Single.error(new ParamException(ErrorCodeEnum.PARAM_ERROR_CODE, "不支持的dsl！"));
                        }
                        Map<String, Object> map = body.getMap();
                        ExecuteCodeService executeCodeService = optional.get();
                        return executeCodeService.execute(clCodeConfig, map);
                    }).subscribe((o, throwable) -> {
                        if (throwable != null) {
                            throwable.printStackTrace();
                        }else{
                            log.info(MessageFormat.format("execute {0},methodName:{1} success", clCodeConfig.getServerAddress(), clCodeConfig.getMethodName()));
                        }
                    });
                });


                return Single.just(true);

            });
        });
    }


}
