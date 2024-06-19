package com.cl.framework.core.utils.config;

import cn.hutool.core.io.IoUtil;
import com.cl.framework.core.constants.FileSuffixConstant;
import com.cl.framework.core.constants.StringsConstant;
import com.cl.framework.core.exception.EmptyStreamException;
import com.cl.framework.core.utils.FileStream;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 配置文件工具类
 *
 * @author xhz
 */
@Slf4j
public class ConfigTool {

   public static ConcurrentMap<String, JsonObject> CONFIG = new ConcurrentHashMap<>();

    private static final ObjectMapper YAML = new YAMLMapper();
    public static String produce(final String env) {
        if (null == env) {
            return "/config/vertx" + StringsConstant.DOT + FileSuffixConstant.YML;
        } else {
            return "/config/vertx" + StringsConstant.DASH + env +
                    StringsConstant.DOT + FileSuffixConstant.YML;
        }
    }

    public static JsonObject read(final String env, Class<?> tClass) throws IOException {
        // 读取 vertx.config 配置
        final JsonObject original = readDirect(produce(env), tClass);
        final JsonObject merged = new JsonObject();
        if (null != original) {
            merged.mergeIn(original, true);
        }
        return merged;
    }



    public static String getString(final InputStream in) throws IOException {
        final StringBuilder buffer = new StringBuilder();
        final BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
        // Character stream
        String line;
        while (null != (line = reader.readLine())) {
            buffer.append(line);
        }
        in.close();
        reader.close();
        return buffer.toString();
    }


    private static JsonObject readDirect(final String filename,Class<?> tClass) throws IOException {
        if (CONFIG.containsKey(filename)) {
            return CONFIG.get(filename);
        } else {
            log.info("开始读取配置文件:{}", filename);
            JsonObject data;
            try {
                JsonNode yamlNode = getYamlNode(filename, tClass);
                data = new JsonObject(yamlNode.toString());
            } catch (IOException e) {
                log.info("读取配置文件出错!", e);
                throw new RuntimeException(e);
            }



            if (!data.isEmpty()) {
                CONFIG.put(filename, data);
            }

            String dockerConfig = System.getenv(StringsConstant.DATACUBE_DOCKER_CONFIG);
            log.info("环境变量读取配置文件:{}", dockerConfig);
            if (StringUtils.isNotBlank(dockerConfig)) {
                InputStream in = null;
                try {
                    in = FileStream.readConfig(dockerConfig, tClass);
                    if (null == in) {
                        throw new EmptyStreamException(dockerConfig);
                    }
                    String fileContent = IoUtil.read(in, Charset.defaultCharset());
                    log.info("环境变量读取配置文件:{}", fileContent);
                    JsonObject dockerConfigJsonObject = new JsonObject(fileContent);
                    data.mergeIn(dockerConfigJsonObject, true);
                } finally {
                    if (in != null) {
                        in.close();
                    }
                }
            }


            return data;
        }
    }


    private static JsonNode getYamlNode(final String filename, Class<?> tClass) throws IOException {
        final InputStream in = FileStream.readConfig(filename, tClass);
        if (null == in) {
            throw new EmptyStreamException(filename);
        }
        JsonNode node = YAML.readTree(in);
        if (null == node) {
            throw new EmptyStreamException(filename);
        }
        return node;
    }
}
