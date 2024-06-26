package com.cl.service.impl;

import com.cl.framework.core.vertx.VertxLauncher;
import com.cl.repository.tables.pojos.ClCodeConfig;
import com.cl.service.ExecuteCodeService;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import io.reactivex.rxjava3.core.Single;
import org.codehaus.groovy.control.CompilerConfiguration;

import java.util.HashMap;
import java.util.Map;

/**
 * GROOVY 代码执行
 *
 * @author xhz
 */
public class GroovyCodeService implements ExecuteCodeService {
    @Override
    public Single<Object> execute(ClCodeConfig clCodeConfig, Map<String, Object> param) {
        return Single.defer(() -> {
            Binding binding = createBinding(param);

            CompilerConfiguration config = new CompilerConfiguration();
            GroovyShell shell = new GroovyShell(binding, config);

            Script script = shell.parse(clCodeConfig.getContent());

            Object[] args = new Object[]{};

            Object o = script.invokeMethod(clCodeConfig.getMethodName(), args);
            if (o == null) {
                return Single.just(new HashMap<>());
            }
            return Single.just(o);
        });

    }

    private static Binding createBinding(Map<String, Object> infos) {
        Binding binding = new Binding();
        if (!infos.isEmpty()) {
            for (Map.Entry<String, Object> entry : infos.entrySet()) {
                binding.setVariable(entry.getKey(), entry.getValue());
            }
        }
        return binding;
    }
}
