package com.cl.framework.core.options.transformer;

import com.cl.framework.core.enums.WallType;
import com.cl.framework.core.options.transformer.wall.JwtWall;
import com.cl.framework.core.options.transformer.wall.MongoWall;
import com.cl.framework.core.utils.reflection.ReflectionUtils;
import com.cl.framework.core.web.core.secure.Cliff;
import io.vertx.core.json.JsonObject;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 表达式解析器
 *
 * @author xhz
 */
public class CliffTransformer implements Transformer<Cliff> {

    private static final ConcurrentMap<WallType, Transformer<Cliff>> WALL_TRANSFORMER =
            new ConcurrentHashMap<WallType, Transformer<Cliff>>() {
                {
                    this.put(WallType.JWT, ReflectionUtils.singleton(JwtWall.class));
                    this.put(WallType.MONGO, ReflectionUtils.singleton(MongoWall.class));
                }
            };

    @Override
    public Cliff transform(JsonObject input) {
        if (input.containsKey("type")) {
            // Standard
            final Transformer<Cliff> transformer = WALL_TRANSFORMER.get(WallType.from(input.getString("type")));
            return transformer.transform(input);
        } else {
            return new Cliff();
        }
    }
}
