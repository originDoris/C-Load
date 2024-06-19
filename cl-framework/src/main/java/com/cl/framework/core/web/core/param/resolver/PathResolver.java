package com.cl.framework.core.web.core.param.resolver;

import com.cl.framework.core.constants.StringsConstant;
import com.cl.framework.core.enums.ErrorCodeEnum;
import com.cl.framework.core.exception.PathAnnoEmptyException;
import com.cl.framework.core.funcation.CubeFn;
import com.cl.framework.core.web.core.agent.Event;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import org.apache.commons.lang3.StringUtils;

import javax.ws.rs.Path;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static cn.hutool.core.util.ReUtil.isMatch;

/**
 * path解析
 *
 * @author xhz
 */
public class PathResolver {

    private static final Logger LOGGER = LoggerFactory.getLogger(PathResolver.class);

    private final static ConcurrentMap<HttpMethod, Set<String>> URIS = new ConcurrentHashMap<HttpMethod, Set<String>>() {
        {
            this.put(HttpMethod.GET, new HashSet<>());
            this.put(HttpMethod.POST, new HashSet<>());
            this.put(HttpMethod.DELETE, new HashSet<>());
            this.put(HttpMethod.PUT, new HashSet<>());
        }
    };

    public static String resolve(final Path path) {
        CubeFn.outError(LOGGER, null == path, PathAnnoEmptyException.class, ErrorCodeEnum.PATH_ANNO_IS_EMPTY);
        return resolve(path, null);
    }


    @SuppressWarnings("all")
    public static String resolve(final Path path, final String root) {
        CubeFn.outError(LOGGER, null == path, PathAnnoEmptyException.class, ErrorCodeEnum.PATH_ANNO_IS_EMPTY);
        return CubeFn.getSemi(StringUtils.isBlank(root), LOGGER, () -> calculate(path(path.value())),
                () -> {
                    final String api = calculate(root);
                    final String contextPath = calculate(path.value());
                    return 1 == api.length() ? path(contextPath) : path(api + contextPath);
                });
    }

    /**
     * 将/query/{name} 解析为/query/:name
     */
    private static String path(final String path) {
        final String regex = "\\{\\w+\\}";
        final Pattern pattern = Pattern.compile(regex);
        final Matcher matcher = pattern.matcher(path);
        String tempStr = path;
        String result = "";
        while (matcher.find()) {
            result = matcher.group();
            // Shift left brace and right brace
            final String replaced = result.trim().substring(1, result.length() - 1);
            tempStr = tempStr.replace(result, ":" + replaced);
        }
        return tempStr;
    }


    private static String calculate(final String path) {
        String uri = path;
        uri = uri.replaceAll("/+", StringsConstant.SLASH);
        if (uri.endsWith(StringsConstant.SLASH)) {
            uri = uri.substring(0, uri.lastIndexOf(StringsConstant.SLASH));
        }
        final String processed = uri;
        final String finalUri = CubeFn.getDefault(null, () -> processed.startsWith(StringsConstant.SLASH)
                ? processed : StringsConstant.SLASH + processed, uri);
        return finalUri;
    }


    public static void resolve(final Event event) {
        resolve(event.getMethod(), event.getPath());
    }

    static void resolve(final HttpMethod method, final String uri) {
        if (Objects.isNull(method)) {
            URIS.keySet().forEach(each -> addSingle(each, uri));
        } else {
            addSingle(method, uri);
        }
    }

    public static String recovery(final String uri, final HttpMethod method) {
        final Set<String> definition = URIS.get(method);
        if (Objects.isNull(definition)) {
            return uri;
        } else {
            return definition.stream()
                    .filter(path -> isMatch(uri, path))
                    .findFirst().orElse(uri);
        }
    }
    private static void addSingle(final HttpMethod method, final String uri) {
        URIS.get(method).add(uri);
    }

}
