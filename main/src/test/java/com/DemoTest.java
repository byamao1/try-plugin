package com;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import spi.IDemo;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import java.util.ServiceLoader;

/**
 * Description:
 *
 * @author: Tom
 * Version: 1.0
 * Create Date Time: 2019-02-25 15:38
 * Update Date Time:
 */
@Slf4j
public class DemoTest {

    // System.getProperty("user.dir") 获取当前工程运行路径
    final String jarPluginDir = System.getProperty("user.dir") + File.separator + "jar-plugin";

    final String classPluginDir = System.getProperty("user.dir") + File.separator + "class-plugin/demo";

    /**
     * jar包插件
     *
     * @throws Exception
     */
    @Test
    public void testJar() throws Exception {
        ClassLoader oldCL = Thread.currentThread().getContextClassLoader();

        // 自定义加载器 或 URLClassLoader都可以用
        ClassLoader newCL = getMyClassLoader(oldCL, jarPluginDir);
//        ClassLoader newCL = getURLClassLoader(oldCL, jarPluginDir);

        // ServiceLoader方式加载
        log.info("ServiceLoader方式加载");
        ServiceLoader<IDemo> serviceLoader = ServiceLoader.load(IDemo.class, newCL);
        List<IDemo> demos = ImmutableList.copyOf(serviceLoader);
        IDemo demo = demos.get(0);
        demo.show();

        // 类加载器方式直接加载
        log.info("类加载器方式直接加载");
        Class cls_demo = newCL.loadClass("com.Demo");
        cls_demo.newInstance();
    }

    private ClassLoader getMyClassLoader(ClassLoader oldCL, String pluginDir) throws MalformedURLException {
        ImmutableList<String> spiPackages = ImmutableList.<String>builder()
                .add("spi.")
                .add("org.slf4j.")
                .build();
        ClassLoader parent = getClass().getClassLoader();
        return new PluginClassLoader(pluginDir, parent, spiPackages);
    }

    private ClassLoader getURLClassLoader(ClassLoader oldCL, String pluginDir) throws MalformedURLException {
        List<URL> list = Lists.newArrayList();
        for (File f : new File(pluginDir).listFiles()) {
            if (f.isFile()) {
                list.add(f.toURI().toURL());
            }
        }
        URL[] urls = new URL[list.size()];
        return new URLClassLoader(list.toArray(urls), Thread.currentThread().getContextClassLoader()); // 父加载器一定要配置，否则会报找不到类 IDemo
    }

    /**
     * class插件
     *
     * @throws Exception
     */
    @Test
    public void testClass() throws Exception {
        ClassLoader oldCL = Thread.currentThread().getContextClassLoader();
        ClassLoader newCL = new URLClassLoader(new URL[]{new File(classPluginDir).toURI().toURL()}, Thread.currentThread().getContextClassLoader());

        // ServiceLoader方式加载
        log.info("ServiceLoader方式加载");
        ServiceLoader<IDemo> serviceLoader = ServiceLoader.load(IDemo.class, newCL);
        List<IDemo> demos = ImmutableList.copyOf(serviceLoader);
        IDemo demo = demos.get(0);
        demo.show();

        // 类加载器方式直接加载
        log.info("类加载器方式直接加载");
        Class cls_demo = newCL.loadClass("com.Demo");
        cls_demo.newInstance();
    }

}

/**
 * 参考Presto
 */
class PluginClassLoader extends URLClassLoader {

    private final ClassLoader spiClassLoader;
    // spi加载类加载的包
    private final List<String> spiPackages;

    public PluginClassLoader(
            String baseDir,
            ClassLoader spiClassLoader,
            Iterable<String> spiPackages) {
        super(getURLs(baseDir), null);

        this.spiClassLoader = spiClassLoader;
        this.spiPackages = ImmutableList.copyOf(spiPackages);
    }

    private static URL[] getURLs(String baseDir) {
        List<URL> list = Lists.newArrayList();
        for (File f : new File(baseDir).listFiles()) {
            try {
                list.add(new URL("file", null, f.getCanonicalPath()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        URL[] res = new URL[list.size()];
        return list.toArray(res);
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        // grab the magic lock
        synchronized (getClassLoadingLock(name)) {
            // Check if class is in the loaded classes cache
            Class<?> cachedClass = findLoadedClass(name);
            if (cachedClass != null) {
                return resolveClass(cachedClass, resolve);
            }

            // If this is an SPI class, only check SPI class loader
            if (isSpiClass(name)) {
                return resolveClass(spiClassLoader.loadClass(name), resolve);
            }

            // Look for class locally
            return super.loadClass(name, resolve);
        }
    }

    private boolean isSpiClass(String name) {
        // todo maybe make this more precise and only match base package
        return spiPackages.stream().anyMatch(name::startsWith);
    }

    private Class<?> resolveClass(Class<?> clazz, boolean resolve) {
        if (resolve) {
            resolveClass(clazz);
        }
        return clazz;
    }
}
