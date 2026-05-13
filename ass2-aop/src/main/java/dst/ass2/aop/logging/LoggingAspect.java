package dst.ass2.aop.logging;

import dst.ass2.aop.IPluginExecutable;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.aop.support.AopUtils;

import java.lang.reflect.Field;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

@Aspect
public class LoggingAspect {

    static {
        Logger root = Logger.getLogger("");
        root.setLevel(Level.INFO);
        for (Handler handler : root.getHandlers()) {
            handler.setLevel(Level.INFO);
        }
    }

    @Around("execution(* dst.ass2.aop.IPluginExecutable+.execute(..)) && !@annotation(dst.ass2.aop.logging.Invisible)")
    public Object logExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        Object target = joinPoint.getTarget();
        String pluginClassName = AopUtils.getTargetClass(target).getName();
        Logger logger = resolveLogger(target);

        log(logger, "Plugin " + pluginClassName + " started to execute");
        try {
            return joinPoint.proceed();
        } finally {
            log(logger, "Plugin " + pluginClassName + " is finished");
        }
    }

    private Logger resolveLogger(Object target) {
        Class<?> current = AopUtils.getTargetClass(target);
        while (current != null && current != Object.class) {
            for (Field field : current.getDeclaredFields()) {
                if (Logger.class.isAssignableFrom(field.getType())) {
                    try {
                        field.setAccessible(true);
                        return (Logger) field.get(target);
                    } catch (IllegalAccessException ignored) {
                        return null;
                    }
                }
            }
            current = current.getSuperclass();
        }
        return null;
    }

    private void log(Logger logger, String message) {
        if (logger != null) {
            logger.info(message);
        } else {
            System.out.println(message);
        }
    }

}
