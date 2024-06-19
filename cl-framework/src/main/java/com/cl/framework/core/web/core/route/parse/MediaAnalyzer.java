package com.cl.framework.core.web.core.route.parse;



import com.cl.framework.core.constants.ErrorInfoConstant;
import com.cl.framework.core.enums.ErrorCodeEnum;
import com.cl.framework.core.exception.WebException;
import com.cl.framework.core.funcation.CubeFn;
import com.cl.framework.core.runtime.Envelop;
import com.cl.framework.core.utils.reflection.ReflectionUtils;
import com.cl.framework.core.web.core.agent.Event;
import com.cl.framework.core.web.core.param.ParamContainer;
import com.cl.framework.core.web.core.param.resolver.JsonResolver;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.rxjava3.ext.web.RoutingContext;
import org.apache.commons.lang3.StringUtils;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.text.MessageFormat;
import java.util.List;
import java.util.Set;

/**
 * 解析 Media
 * @author xhz
 */
public class MediaAnalyzer implements Analyzer {

    private static final Logger LOGGER = LoggerFactory.getLogger(JsonResolver.class);

    private final transient Income<List<ParamContainer<Object>>> income = ReflectionUtils.singleton(ParamContainerIncome.class);

    @Override
    public Object[] in(final RoutingContext context,
                       final Event event)
            throws WebException {
        /* Consume mime type matching **/
        final MediaType requestMedia = this.getMedia(context);
        accept(event, requestMedia);

        /* Extract definition from method **/
        final List<ParamContainer<Object>> epsilons = this.income.in(context, event);

        /* Extract value list **/
        return epsilons.stream()
                .map(ParamContainer::getValue).toArray();
    }

    @Override
    public Envelop out(final Envelop envelop,
                       final Event event) throws WebException {
        // TODO: Replier
        return null;
    }

    private MediaType getMedia(final RoutingContext context) {
        final String header = context.request().getHeader(HttpHeaders.CONTENT_TYPE);
        return CubeFn.getSemi(StringUtils.isBlank(header), LOGGER,
                () -> MediaType.WILDCARD_TYPE,
                () -> MediaType.valueOf(header));
    }

    private void accept(final Event event,
                       final MediaType type) throws WebException {
        final Set<MediaType> medias = event.getConsumes();
        if (!medias.contains(MediaType.WILDCARD_TYPE)) {
            boolean match = medias.stream()
                    .anyMatch(media ->
                            MediaType.MEDIA_TYPE_WILDCARD.equals(media.getType()) ||
                                    media.getType().equalsIgnoreCase(type.getType()));
            CubeFn.outError(LOGGER, !match,
                    WebException.class,
                    ErrorCodeEnum.MEDIA_NOT_SUPPORT_ERROR,
                    MessageFormat.format(ErrorInfoConstant.MEDIA_NOT_SUPPORT_ERROR, type, medias));
            match = medias.stream()
                    .anyMatch(media ->
                            MediaType.MEDIA_TYPE_WILDCARD.equals(media.getSubtype()) ||
                                    media.getSubtype().equalsIgnoreCase(type.getSubtype())
                    );
            CubeFn.outError(LOGGER,!match ,
                    WebException.class,
                    ErrorCodeEnum.MEDIA_NOT_SUPPORT_ERROR,
                    MessageFormat.format(ErrorInfoConstant.MEDIA_NOT_SUPPORT_ERROR, type, medias));
        }
    }

}
