package com.cl.framework.core.options.transformer;

import com.cl.framework.core.options.DataBaseOptions;
import io.vertx.core.json.JsonObject;

/**
 * DataBaseTransformer
 *
 * @author xhz
 */
public class DataBaseTransformer implements Transformer<DataBaseOptions>{
    @Override
    public DataBaseOptions transform(JsonObject input) {
        return input == null ? new DataBaseOptions() : new DataBaseOptions(input);
    }
}
