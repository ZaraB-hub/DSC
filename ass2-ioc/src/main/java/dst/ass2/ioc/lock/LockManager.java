package dst.ass2.ioc.lock;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantLock;

public final class LockManager {

    private static final LockManager INSTANCE = new LockManager();

    private final ConcurrentMap<String, ReentrantLock> locks = new ConcurrentHashMap<>();

    private LockManager() {
    }

    public static LockManager getInstance() {
        return INSTANCE;
    }

    public ReentrantLock getLock(String name) {
        return locks.computeIfAbsent(name, k -> new ReentrantLock());

    }
}