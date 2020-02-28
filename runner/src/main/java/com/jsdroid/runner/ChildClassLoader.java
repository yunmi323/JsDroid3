package com.jsdroid.runner;

public class ChildClassLoader extends ClassLoader {
    private ClassLoader parent;
    private ClassLoader child;

    public ChildClassLoader(ClassLoader parent, ClassLoader child) {
        super(null);
        this.parent = parent;
        this.child = child;
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        try {
            Class<?> aClass = child.loadClass(name);
            if (aClass != null) {
                return aClass;
            }
        } catch (Throwable e) {
        }
        return parent.loadClass(name);
    }
}
