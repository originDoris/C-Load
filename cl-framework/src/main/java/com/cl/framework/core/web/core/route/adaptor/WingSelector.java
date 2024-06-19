package com.cl.framework.core.web.core.route.adaptor;

import com.cl.framework.core.web.core.Pool;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;

import javax.ws.rs.core.MediaType;
import java.text.MessageFormat;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

/**
 * @author xhz
 */
public class WingSelector {

    private static final Logger LOGGER = LoggerFactory.getLogger(WingSelector.class);

    public static Wings end(final String contentType, final Set<MediaType> produces) {

        final MediaType type;
        if (Objects.isNull(contentType)) {
            type = MediaType.WILDCARD_TYPE;
        } else {
            type = MediaType.valueOf(contentType);
        }
        final ConcurrentMap<String, Wings> subtype = Pool.SELECT_POOL.get(type.getType());
        final Wings selected;
        if (Objects.isNull(subtype) || subtype.isEmpty()) {
            selected = Pool.SELECT_POOL.get(MediaType.APPLICATION_JSON_TYPE.getType())
                    .get(MediaType.APPLICATION_JSON_TYPE.getSubtype());
        } else {
            final Wings wings = subtype.get(type.getSubtype());
            selected = Objects.isNull(wings) ? new JsonWings() : wings;
        }

        LOGGER.info(MessageFormat.format("Wings response selected `{0}` for content type {1}, mime = {2}",
                selected.getClass().getName(), contentType, type.toString()));
        return selected;
    }
}
