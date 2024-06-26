package com.cl.context;

import com.cl.service.ExecuteCodeService;
import com.cl.service.impl.GroovyCodeService;
import io.reactivex.rxjava3.core.Single;

import javax.inject.Inject;
import java.util.Optional;

import static com.cl.constant.DslTypeConstant.*;

/**
 * 表达式解析器
 *
 * @author xhz
 */
public class ExecuteContext {

    @Inject
    private GroovyCodeService groovyCodeService;


    public Single<Optional<ExecuteCodeService>> getExecuteService(String type) {
        switch (type) {
            case GROOVY:
                return Single.just(Optional.of(groovyCodeService));
            default:
                return Single.just(Optional.empty());
        }
    }
}
