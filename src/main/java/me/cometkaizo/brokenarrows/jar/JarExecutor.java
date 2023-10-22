package me.cometkaizo.brokenarrows.jar;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

/**
 * A class for using classes in a jar file without writing it to disk
 * @see <a href="https://stackoverflow.com/questions/28964450/loading-a-jar-dynamically-from-memory">stackoverflow.com</a>
 */
public class JarExecutor {
    protected final JarInputStream input;
    protected ClassLoader classLoader;
    protected Map<String, Class<?>> jarClasses;
    protected final Function<String, String> classNameFunction;

    public JarExecutor(JarInputStream input, Function<String, String> classFilter) {
        this.input = Objects.requireNonNull(input);
        this.classNameFunction = classFilter;
    }

    public JarExecutor(JarInputStream input) {
        this(input, ClassFilter.ALL_CLASSES);
    }


    public Class<?> resolve(ClassSupplier classSup) {
        try {
            loadClasses(false);
            return classSup.get(jarClasses);
        } catch (ClassNotFoundException e) {
            throw new JarExecutionException(e);
        }
    }

    public Class<?> resolveClass(String name) {
        return resolve(ClassSupplier.of(name));
    }

    public Field resolve(FieldSupplier fieldSup) {
        try {
            loadClasses(false);
            return fieldSup.get(jarClasses);
        } catch (ClassNotFoundException | NoSuchFieldException e) {
            throw new JarExecutionException(e);
        }
    }

    public Method resolve(MethodSupplier methodSup) {
        try {
            loadClasses(false);
            return methodSup.get(jarClasses);
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            throw new JarExecutionException(e);
        }
    }

    public Constructor<?> resolve(ConstructorSupplier constructorSup) {
        try {
            loadClasses(false);
            return constructorSup.get(jarClasses);
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            throw new JarExecutionException(e);
        }
    }

    public Object invokeStatic(MethodSupplier methodSup, Object... args) {
        return invokeMethod(methodSup, null, args);
    }

    public Object invokeMethod(MethodSupplier methodSup, Object instance, Object... args) {
        Method method = resolve(methodSup);
        try {
            return method.invoke(instance, args);
        } catch (InvocationTargetException e) {
            throw new JarExecutionException("Exception in forge method " + method.getName(), e);
        } catch (IllegalAccessException e) {
            throw new JarExecutionException("Could not access method " + method.getName(), e);
        }
    }

    public Object invokeConstructor(ConstructorSupplier constructorSup, Object... args) {
        Constructor<?> constructor = resolve(constructorSup);
        try {
            return constructor.newInstance(args);
        } catch (InvocationTargetException e) {
            throw new JarExecutionException("Exception in forge method " + constructor.getName(), e);
        } catch (IllegalAccessException e) {
            throw new JarExecutionException("Could not access method " + constructor.getName(), e);
        } catch (InstantiationException e) {
            throw new JarExecutionException("Abstract class cannot be instantiated: " + constructor.getDeclaringClass().getName(), e);
        }
    }


    public void loadClasses(boolean force) throws ClassNotFoundException {
        if (jarClasses != null && !force) return;
        jarClasses = new HashMap<>();
        final Map<String, byte[]> map = new HashMap<>();

        try (input) {
            while (true) {
                JarEntry nextEntry = input.getNextJarEntry();
                if (nextEntry == null) break;

                final int est = (int) nextEntry.getSize();
                byte[] data = new byte[est > 0? est : 1024];
                int real = 0;
                for (int r = input.read(data); r > 0; r = input.read(data, real, data.length - real))
                    if (data.length == (real += r)) data = Arrays.copyOf(data, data.length * 2);
                if (real != data.length) data = Arrays.copyOf(data, real);
                map.put("/" + nextEntry.getName(), data);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        URL u = getJarUrl(map);
        classLoader = new URLClassLoader(new URL[]{u});

        for (String fileName : map.keySet()) {
            String className = classNameFunction.apply(fileName);
            if (className != null) try {
                Class<?> forgeClass = classLoader.loadClass(className);
                jarClasses.put(forgeClass.getName(), forgeClass);
            } catch (ClassNotFoundException e) {
                jarClasses.clear();
                throw e;
            }
        }
    }

    private static URL getJarUrl(Map<String, byte[]> map) {
        try {
            return new URL("x-buffer", null, -1, "/", new URLStreamHandler() {
                protected URLConnection openConnection(URL u) throws IOException {
                    final byte[] data = map.get(u.getFile());
                    if (data==null) throw new FileNotFoundException(u.getFile());
                    return new URLConnection(u) {
                        public void connect() {}
                        @Override
                        public InputStream getInputStream() {
                            return new ByteArrayInputStream(data);
                        }
                    };
                }
            });
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public enum ClassFilter implements Function<String, String> {
        ALL_CLASSES(s -> s.endsWith(".class") && !s.contains("$") ? s.replace('/', '.').substring(1, s.length() - 6) : null);

        private final Function<String, String> func;
        ClassFilter(Function<String, String> func) {
            this.func = func;
        }
        @Override
        public String apply(String fileName) {
            return func.apply(fileName);
        }
    }

}
