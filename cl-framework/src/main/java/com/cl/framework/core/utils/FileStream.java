package com.cl.framework.core.utils;

import com.cl.framework.core.constants.FileProtocolsConstant;
import com.cl.framework.core.constants.FileSuffixConstant;
import com.cl.framework.core.exception.EmptyStreamException;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * @Description: 读取文件数据
 * @author xhz
 */
@Slf4j
public class FileStream {

    private FileStream() {
    }

    public static InputStream readConfig(final String filename, Class<?> clazz) {
        try {
            return read(filename, clazz);
        } catch (FileNotFoundException e) {
            log.info("读取文件出现错误！", e);
            return null;
        }
    }


    /**
     * 根据fileName 读取配置文件
     */
    private static InputStream read(final String fileName,
                                    final Class<?> clazz) throws FileNotFoundException {
        final File file = new File(fileName);
        if (file.exists()) {
            log.warn("当前路径被系统扫描，文件存在? {}.", file.exists());
        }
        InputStream in;
        if (file.exists()) {
            in = in(file);
        }else{
            in = (null == clazz) ? in(fileName) : in(fileName, clazz);
        }
        if (null == in) {
            in = readSupplier(() -> FileStream.class.getResourceAsStream(fileName), fileName);
        }
        if (null == in) {
            in = readSupplier(() -> ClassLoader.getSystemResourceAsStream(fileName), fileName);
        }

        // 判断是否是jar包
        if (null == in && fileName.contains(FileSuffixConstant.JAR_DIVIDER)) {
            in = readJar(fileName);
        }
        if (null == in) {
            throw new EmptyStreamException(fileName);
        }
        return in;
    }

    private static InputStream readJar(final String filename) {
        return readSupplier(() -> {
            try {
                final URL url = new URL(filename);
                final String protocol = url.getProtocol();
                if (FileProtocolsConstant.JAR.equals(protocol)) {
                    final JarURLConnection jarCon = (JarURLConnection) url.openConnection();
                    return jarCon.getInputStream();
                } else {
                    return null;
                }
            } catch (final IOException e) {
                log.warn("读取jar文件出错：", e);
                return null;
            }
        }, filename);
    }

    private static InputStream readSupplier(final Supplier<InputStream> supplier,
                                            final String filename) {
        final InputStream in = supplier.get();
        if (null != in) {
            log.warn("读取文件输入流为空! fileName:{}", filename);
        }
        return in;
    }


    public static InputStream in(final File file) throws FileNotFoundException {
        return  (file.exists() && file.isFile()) ? new FileInputStream(file) : null;
    }

    public static InputStream in(final String filename,
                          final Class<?> clazz) {
        return clazz.getResourceAsStream(filename);
    }

    public static InputStream in(final String filename) {
        final ClassLoader loader = Thread.currentThread().getContextClassLoader();
        return loader.getResourceAsStream(filename);
    }
}
