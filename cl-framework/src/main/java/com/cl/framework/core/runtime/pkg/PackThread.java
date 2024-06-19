package com.cl.framework.core.runtime.pkg;

import com.cl.framework.core.constants.ErrorInfoConstant;
import com.cl.framework.core.constants.MessageConstant;
import com.cl.framework.core.utils.reflection.ReflectionUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Set;

/**
 * Package scan thread
 *
 * @author xhz
 */
@EqualsAndHashCode(callSuper = true)
@Slf4j
@Data
public class PackThread extends Thread {
    private final transient String pkg;
    @Getter
    private final Set<Class<?>> classes = new HashSet<>();

    public PackThread(final String pkg) {
        this.setName("package-scanner-" + super.getId());
        this.pkg = pkg;
    }

    @Override
    public void run() {
        try {
            Set<Class<?>> scanned = ReflectionUtils.scanClasses(this.pkg);
            this.classes.addAll(scanned);
        } catch (IOException | ClassNotFoundException e) {
            log.info(MessageFormat.format(ErrorInfoConstant.SCAN_PACKAGE_ERROR, getName(), this.pkg), e);
            throw new RuntimeException(e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

}
