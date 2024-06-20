package com.cl.service;

import com.cl.framework.core.domain.BasePage;
import com.cl.framework.plugin.jooq.JooqInfix;
import com.cl.model.query.CodeConfigQuery;
import com.cl.repository.tables.daos.ClCodeConfigDao;
import com.cl.repository.tables.pojos.ClCodeConfig;
import io.reactivex.rxjava3.core.Single;

/**
 * CodeConfigService
 *
 * @author xhz
 */
public interface CodeConfigService extends BaseService<ClCodeConfigDao> {


    Single<Boolean> save(ClCodeConfig clCodeConfig);

    Single<Boolean> remove(Long id);

    Single<BasePage<ClCodeConfig>> query(CodeConfigQuery query);

    Single<Boolean> publish(Long id);



    @Override
    default ClCodeConfigDao getDao() {
        return JooqInfix.getDao(ClCodeConfigDao.class);
    }
}
