package com.cl.framework.core.web.core.job;


import com.cl.framework.core.web.core.worker.Mission;

import java.util.Set;

/**
 * 用于读取job
 * @author xhz
 */
interface JobReader {

    Set<Mission> fetch();


    Mission fetch(String code);
}
