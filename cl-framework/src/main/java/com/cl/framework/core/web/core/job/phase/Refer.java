package com.cl.framework.core.web.core.job.phase;

import com.cl.framework.core.funcation.CubeFn;
import io.reactivex.rxjava3.core.Single;

/**
 * 存储方法引用
 * @author xhz
 */
@SuppressWarnings("all")
public class Refer {

    private Object reference;

    public <T> Refer add(final T reference) {
        this.reference = reference;
        return this;
    }

    public <T> Single<T> single(final T reference) {
        this.reference = reference;
        return Single.just(reference);
    }

    public boolean successed() {
        return null != this.reference;
    }

    public <T> T get() {
        return CubeFn.getDefault(null, () -> (T) this.reference, this.reference);
    }
}
