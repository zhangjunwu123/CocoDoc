package main.java.com.jd.coco.util;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileFilter;
import java.util.*;

/**
 * 类操作工具类
 */
public final class CustomClassUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomClassUtil.class);

    /**
     * 获取类加载器
     * 获取加载器类的实现比较简单，只需获取当前线程的ClassLoader
     */
    public static ClassLoader getClassLoader() {
        //return BootstrapClassLoaderUtil.initClassLoader().loadClass();

        //return Thread.currentThread().getContextClassLoader();
        return ClassLoader.getSystemClassLoader();
    }

    /**
     * 加载类
     * 加载类需要提供类名与是否初始化的标志，这里提到的初始化指是否执行类的静态代码块;
     * 为了提高加载类的性能，可以将loadClass方法的isInitialized参数设置false
     */
    public static Class<?> loadClass(String className, boolean isInitialized) {
        Class<?> cls = null;
        try {
            //进行类加载
            cls = Class.forName(className, isInitialized, getClassLoader());
            //PsiClassInitializer
        } catch (ClassNotFoundException e) {
            LOGGER.error("load class failure.", e);
            throw new RuntimeException(e);
        }
        return cls;
    }

    /**
     * 获取指定目录下所有PsiFile，根据模块来区分，放入到map中<moduleName, Set<PsiFiles>>
     */
    public static Map<String, Set<PsiFile>> getModuleFilesMap(PsiFile psiFile, VirtualFile virtualFile,
                Project project) {

        Set<String> modules = new HashSet<String>();
        Map<String, Set<PsiFile>> moduleFilesMap = new HashMap<>();
        try {
            modules = getModules(psiFile, virtualFile, modules);

            if(CollectionUtils.isEmpty(modules)){
                return moduleFilesMap;
            }
            for (String moduleName : modules) {
                Set<PsiFile> psiFileSet = new HashSet<PsiFile>();
                psiFileSet = getPsiFileSetInModule(virtualFile, psiFileSet, moduleName, project);

                moduleFilesMap.put(moduleName, psiFileSet);
            }

        } catch (Exception e) {
            LOGGER.error("get class set failure.", e);
            throw new RuntimeException(e);
        }
        return moduleFilesMap;
    }

    /*
    * 获取所有的module的名称set
    * */
    private static Set<String> getModules(PsiFile psiFile, VirtualFile virtualFile, Set<String> modules) {
        VirtualFile[] files = virtualFile.getChildren();
        if(null != psiFile){//证明选择是文件
            if(psiFile.getName().endsWith(".java")){
                VirtualFile virtualFileFromFile = psiFile.getVirtualFile();
                dealModuleNameFromFile(modules, virtualFileFromFile);

            }else if(psiFile.getName().endsWith(".xml")){
                //TODO 2.0版本增加dubbo等xml解析
            }

        }else{//选择是目录
            for (VirtualFile file : files) {
                if(file.isDirectory()){
                    getModules(null, file, modules);
                    continue;
                }
                dealModuleNameFromFile(modules, file);
            }
        }

        return modules;
    }

    //根据PsiFile的文件名来获取moduleName
    private static void dealModuleNameFromFile(Set<String> moduleSet, VirtualFile file) {
        if(file.getName().endsWith(".java")){
            String[] parentArray = file.getPath().split("src")[0].split("/");
            String moduleName = parentArray[parentArray.length-1];
            moduleSet.add(moduleName);
        }else if(file.getName().endsWith(".xml")){
            //TODO 2.0
        }
    }

    //获取某个模块module下的所有PsiFiles
    private static Set<PsiFile> getPsiFileSetInModule(VirtualFile virtualFile, Set<PsiFile> psiFileSet,
                                                      String moduleName, Project project)  {
        VirtualFile[] virtualFiles = virtualFile.getChildren();
        if(virtualFiles.length > 0){//是目录
            for (VirtualFile file : virtualFiles) {
                if(file.isDirectory()){
                    getPsiFileSetInModule(file, psiFileSet,moduleName, project);
                    continue;
                }
                addPsiFileSetIfTrue(psiFileSet, moduleName, project, file);
            }
        }else{//单个文件
            addPsiFileSetIfTrue(psiFileSet, moduleName, project, virtualFile);
        }

        return psiFileSet;
    }

    //如果VirtualFile是PsiFile的话就添加到PsiFileSet中
    private static void addPsiFileSetIfTrue(Set<PsiFile> psiFileSet, String moduleName, Project project, VirtualFile file) {
        if (file.getName().endsWith(".java")) {
            String[] parentArray = file.getPath().split("src")[0].split("/");
            String moduleClassName = parentArray[parentArray.length - 1];
            if (moduleName.equals(moduleClassName)) {
                PsiFile psiFile = PsiManager.getInstance(project).findFile(file);
                psiFileSet.add(psiFile);
            }
        }

    }

    private static void addClass(Set<Class<?>> classSet, String packagePath, String packageName) {
        File[] files = new File(packagePath).listFiles(new FileFilter() {
            public boolean accept(File file) {
                return (file.isFile() && file.getName().endsWith(".class")) || file.isDirectory();
            }
        });
        for (File file : files) {
            String fileName = file.getName();
            if (file.isFile()) {
                String className = fileName.substring(0, fileName.lastIndexOf("."));
                if (StringUtils.isNotEmpty(packageName)) {
                    className = packageName + "." + className;
                }
                doAddClass(classSet, className);
            } else {
                String subPackagePath = fileName;
                if (StringUtils.isNotEmpty(packageName)){
                    subPackagePath = packagePath +"/"+subPackagePath;
                }
                String subPackageName = fileName;
                if (StringUtils.isNotEmpty(packageName)){
                    subPackageName = packageName +"."+subPackageName;
                }
                addClass(classSet,subPackagePath,subPackageName);
            }
        }
    }

    private static void doAddClass(Set<Class<?>> classSet, String className) {
        Class<?> cls = loadClass(className, false);
        PsiClass psiClass = null;
        classSet.add(cls);
    }
}
