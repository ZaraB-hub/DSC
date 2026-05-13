package dst.ass2.aop.management;

import dst.ass2.aop.IPluginExecutable;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.ExecutorService;

@Aspect
public class ManagementAspect {

    private static final ScheduledExecutorService WATCHDOG = Executors.newSingleThreadScheduledExecutor(runnable -> {
        Thread thread = new Thread(runnable, "plugin-timeout-watchdog");
        thread.setDaemon(true);
        return thread;
    });
    private static final ExecutorService INTERRUPT_POOL = Executors.newCachedThreadPool(runnable -> {
        Thread thread = new Thread(runnable, "plugin-timeout-interrupt");
        thread.setDaemon(true);
        return thread;
    });

    @Around("execution(* dst.ass2.aop.IPluginExecutable+.execute(..)) && @annotation(timeout)")
    public Object enforceTimeout(ProceedingJoinPoint joinPoint, Timeout timeout) throws Throwable {
        if (timeout.value() <= 0) {
            return joinPoint.proceed();
        }

        Object target = joinPoint.getTarget();
        AtomicBoolean finished = new AtomicBoolean(false);

        WATCHDOG.schedule(() -> {
            if (!finished.get()) {
                INTERRUPT_POOL.execute(() -> ((IPluginExecutable) target).interrupted());
            }
        }, timeout.value(), TimeUnit.MILLISECONDS);

        try {
            return joinPoint.proceed();
        } finally {
            finished.set(true);
        }
    }

}
