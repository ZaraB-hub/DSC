package dst.ass2.ioc.lock;

import dst.ass2.ioc.di.annotation.Component;
import javassist.*;
import javassist.bytecode.AnnotationsAttribute;

import java.io.ByteArrayInputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

public class LockingInjector implements ClassFileTransformer {

    private static final String REENTRANT_LOCK ="java.util.concurrent.locks.ReentrantLock";

    private static final String LOCK_MANAGER =LockManager.class.getName();

    @Override
    public byte[] transform(ClassLoader loader,
                            String className,
                            Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain,
                            byte[] classfileBuffer)
            throws IllegalClassFormatException {

        if (loader == null || className == null) {
            return classfileBuffer;
        }

        try {
            ClassPool pool = new ClassPool(true);
            pool.insertClassPath(new LoaderClassPath(loader));

            CtClass ctClass =
                    pool.makeClass(new ByteArrayInputStream(classfileBuffer));

            try {

                if (!ctClass.hasAnnotation(Component.class)) {
                    return classfileBuffer;
                }

                boolean transformed = false;

                for (CtMethod method : ctClass.getDeclaredMethods()) {

                    Lock lockAnnotation = getLockAnnotation(method);

                    if (lockAnnotation == null) {
                        continue;
                    }

                    if (Modifier.isAbstract(method.getModifiers())
                            || Modifier.isNative(method.getModifiers())) {
                        continue;
                    }

                    instrumentMethod(ctClass, method, lockAnnotation.value());

                    transformed = true;
                }

                return transformed
                        ? ctClass.toBytecode()
                        : classfileBuffer;

            } finally {
                ctClass.detach();
            }

        } catch (Exception e) {
            IllegalClassFormatException ex =
                    new IllegalClassFormatException(e.getMessage());

            ex.initCause(e);
            throw ex;
        }
    }

    private Lock getLockAnnotation(CtMethod method) {

        try {
            if (!method.hasAnnotation(Lock.class)) {
                return null;
            }

            return (Lock) method.getAnnotation(Lock.class);

        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    private void instrumentMethod(CtClass ctClass,
                                  CtMethod originalMethod,
                                  String lockName) throws Exception {

        String originalName = originalMethod.getName();
        String implName = originalName + "$impl";

        // rename
        originalMethod.setName(implName);

        // wrapper method
        CtMethod wrapper =
                CtNewMethod.copy(originalMethod, originalName, ctClass, null);

        String body;

        if (originalMethod.getReturnType().equals(CtClass.voidType)) {

            body =
                    "{ " +
                            REENTRANT_LOCK + " lock = " +
                            LOCK_MANAGER +
                            ".getInstance().getLock(\"" +
                            escape(lockName) +
                            "\");" +

                            "lock.lock();" +

                            "try { " +
                            implName + "($$);" +
                            "} finally { " +
                            "lock.unlock();" +
                            "} " +
                            "}";

        } else {

            body =
                    "{ " +
                            REENTRANT_LOCK + " lock = " +
                            LOCK_MANAGER +
                            ".getInstance().getLock(\"" +
                            escape(lockName) +
                            "\");" +

                            "lock.lock();" +

                            "try { " +
                            "return " + implName + "($$);" +
                            "} finally { " +
                            "lock.unlock();" +
                            "} " +
                            "}";
        }

        wrapper.setBody(body);

        ctClass.addMethod(wrapper);
    }

    private String escape(String value) {
        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"");
    }
}