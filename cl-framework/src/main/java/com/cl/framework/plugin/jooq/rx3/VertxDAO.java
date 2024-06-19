package com.cl.framework.plugin.jooq.rx3;

import com.cl.framework.plugin.jooq.shared.GenericVertxDAO;
import io.reactivex.rxjava3.core.Single;
import org.jooq.UpdatableRecord;

import java.util.List;
import java.util.Optional;

/**
 * VertxDAO
 *
 * @author xhz
 */
public interface VertxDAO<R extends UpdatableRecord<R>, P, T> extends GenericVertxDAO<R, P, T, Single<List<P>>, Single<Optional<P>>, Single<Integer>, Single<T>> {
}
