package com.cl.framework.core.runtime;

import com.cl.framework.core.constants.MessageConstant;
import com.cl.framework.core.runtime.pkg.PackHunter;
import com.cl.framework.core.runtime.pkg.PackThread;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.vertx.core.impl.ConcurrentHashSet;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.Set;

/**
 * 负责包扫描
 *
 * @author xhz
 */
@Slf4j
public final class DataTablePack {

    private static final Set<Class<?>> CLASSES = new ConcurrentHashSet<>();

    private DataTablePack() {
    }


    public static Set<Class<?>> getClasses(String[] filters) {
        if (CLASSES.isEmpty()) {
            final Set<String> packageDirs = PackHunter.getPackages(filters);
            CLASSES.addAll(multiClasses(packageDirs.toArray(new String[]{})));
            log.info(MessageConstant.CLASSES, CLASSES.size());
        }
        return CLASSES;
    }



    private static Set<Class<?>> multiClasses(final String[] packageDir) {
        final Set<PackThread> references = new HashSet<>();
        final Disposable disposable = Observable.fromArray(packageDir)
                .map(PackThread::new)
                .map(item -> {
                    references.add(item);
                    return item;
                })
                .subscribe(Thread::start);
        final Set<Class<?>> result = new HashSet<>();
        try {
            for (final PackThread item : references) {
                item.join();
            }
            for (final PackThread thread : references) {
                result.addAll(thread.getClasses());
            }
            if (!disposable.isDisposed()) {
                disposable.dispose();
            }
        } catch (final InterruptedException ex) {
            log.warn("扫描包下的类出现错误,", ex);
        } catch (final Throwable ex) {
            ex.printStackTrace();
        }
        return result;
    }
}
