package de.christophkraemer.rhino.javascript;

import org.mozilla.javascript.ClassShutter;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.WrapFactory;

public class ContextFactoryBuilder {
    private Integer optimizationLevel;
    private WrapFactory wrapFactory;
    private ClassShutter classShutter;
    private Integer languageVersion;

    public ContextFactoryBuilder setOptimizationLevel(int optimizationLevel) {
        this.optimizationLevel = optimizationLevel;
        return this;
    }

    public ContextFactoryBuilder setWrapFactory(WrapFactory wrapFactory) {
        this.wrapFactory = wrapFactory;
        return this;
    }

    public ContextFactoryBuilder setClassShutter(ClassShutter classShutter) {
        this.classShutter = classShutter;
        return this;
    }

    public ContextFactoryBuilder setLanguageVersion(Integer languageVersion) {
        this.languageVersion = languageVersion;
        return this;
    }


    public ContextFactory build() {
        return new ContextFactoryImpl(optimizationLevel == null? -1: optimizationLevel,
                wrapFactory == null ? RhinoWrapFactory.getInstance(): wrapFactory,
                classShutter == null? RhinoClassShutter.getInstance(): classShutter,
                languageVersion == null ? Context.VERSION_1_8: languageVersion);
    }



    private static class ContextFactoryImpl extends DefaultContextFactory {
        private final int optimizationLevel;
        private final WrapFactory wrapFactory;
        private final ClassShutter classShutter;
        private int languageVersion;

        public ContextFactoryImpl(int optimizationLevel, WrapFactory wrapFactory, ClassShutter classShutter, int languageVersion) {
            this.optimizationLevel = optimizationLevel;
            this.wrapFactory = wrapFactory;
            this.classShutter = classShutter;
            this.languageVersion = languageVersion;
        }

        @Override
        protected Context makeContext() {
            Context cx = super.makeContext();
            cx.setLanguageVersion(languageVersion);
            cx.setOptimizationLevel(optimizationLevel);
            cx.setClassShutter(classShutter);
            cx.setWrapFactory(wrapFactory);
            return cx;
        }
    }
}
