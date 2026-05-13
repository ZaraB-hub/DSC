package dst.ass2.aop.impl;

import dst.ass2.aop.IPluginExecutable;
import dst.ass2.aop.IPluginExecutor;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public final class PluginExecutor implements IPluginExecutor {

    private static final long POLL_INTERVAL_MS = 100L;

    private final Set<File> monitoredDirectories = ConcurrentHashMap.newKeySet();
    private final ConcurrentHashMap<String, FileSnapshot> seenSnapshots = new ConcurrentHashMap<>();
    private final ExecutorService pluginPool = Executors.newCachedThreadPool();
    private final ScheduledExecutorService monitorPool = Executors.newSingleThreadScheduledExecutor();

    private volatile boolean started;

    @Override
    public void monitor(File dir) {
        if (dir != null) {
            monitoredDirectories.add(dir);
        }
    }

    @Override
    public void stopMonitoring(File dir) {
        if (dir != null) {
            monitoredDirectories.remove(dir);
        }
    }

    @Override
    public synchronized void start() {
        if (started) {
            return;
        }
        started = true;
        scanMonitoredDirectories();
        monitorPool.scheduleWithFixedDelay(this::safeScan, POLL_INTERVAL_MS, POLL_INTERVAL_MS, TimeUnit.MILLISECONDS);
    }

    @Override
    public synchronized void stop() {
        started = false;
        monitorPool.shutdownNow();
        pluginPool.shutdownNow();
        try {
            monitorPool.awaitTermination(1, TimeUnit.SECONDS);
            pluginPool.awaitTermination(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void safeScan() {
        if (!started) {
            return;
        }
        try {
            scanMonitoredDirectories();
        } catch (Exception ignored) {


        }
    }

    private void scanMonitoredDirectories() {
        monitoredDirectories.stream()
            .filter(File::isDirectory)
            .sorted(Comparator.comparing(File::getAbsolutePath))
            .forEach(this::scanDirectory);
    }

    private void scanDirectory(File directory) {
        File[] jarFiles = directory.listFiles(file -> file.isFile() && file.getName().toLowerCase().endsWith(".jar"));
        if (jarFiles == null || jarFiles.length == 0) {
            return;
        }

        Arrays.sort(jarFiles, Comparator.comparing(File::getAbsolutePath));
        for (File jarFile : jarFiles) {
            FileSnapshot snapshot = FileSnapshot.of(jarFile);
            String path = jarFile.getAbsolutePath();
            FileSnapshot previous = seenSnapshots.putIfAbsent(path, snapshot);
            if (previous == null || !previous.equals(snapshot)) {
                seenSnapshots.put(path, snapshot);
                submitJar(jarFile);
            }
        }
    }

    private void submitJar(File jarFile) {
        String[] classNames = listClassNames(jarFile);
        for (String className : classNames) {
            pluginPool.submit(() -> executePlugin(jarFile, className));
        }
    }

    private String[] listClassNames(File jarFile) {
        try (java.util.jar.JarFile jar = new java.util.jar.JarFile(jarFile)) {
            return jar.stream()
                .filter(entry -> entry.getName().endsWith(".class"))
                .map(entry -> entry.getName().substring(0, entry.getName().length() - 6).replace('/', '.'))
                .sorted()
                .toArray(String[]::new);
        } catch (IOException e) {
            return new String[0];
        }
    }

    private void executePlugin(File jarFile, String className) {
        try (URLClassLoader loader = new URLClassLoader(new URL[]{jarFile.toURI().toURL()}, IPluginExecutable.class.getClassLoader())) {
            Class<?> pluginClass = Class.forName(className, false, loader);
            if (!IPluginExecutable.class.isAssignableFrom(pluginClass)
                || pluginClass.isInterface()
                || Modifier.isAbstract(pluginClass.getModifiers())) {
                return;
            }

            Constructor<?> constructor = pluginClass.getDeclaredConstructor();
            constructor.setAccessible(true);
            IPluginExecutable executable = (IPluginExecutable) constructor.newInstance();
            executable.execute();
        } catch (Exception ignored) {
            // Individual plugins are isolated; failures should not stop the executor.
        }
    }

    private static final class FileSnapshot {
        private final long lastModified;
        private final long length;

        private FileSnapshot(long lastModified, long length) {
            this.lastModified = lastModified;
            this.length = length;
        }

        static FileSnapshot of(File file) {
            return new FileSnapshot(file.lastModified(), file.length());
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof FileSnapshot)) {
                return false;
            }
            FileSnapshot other = (FileSnapshot) obj;
            return lastModified == other.lastModified && length == other.length;
        }

        @Override
        public int hashCode() {
            return Long.hashCode(lastModified) * 31 + Long.hashCode(length);
        }
    }
}