package de.christophkraemer.rhino.javascript;

import org.mozilla.javascript.*;

import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;

/**
 * @author Andrey Kulikov (ankulikov)
 * @date 05.04.2018
 */
public class DefaultContextFactory extends ContextFactory {

    private static final String RHINO_JS_VERSION = "rhino.js.version";
    private static final int languageVersion = getLanguageVersion();
    private static final String RHINO_OPT_LEVEL = "rhino.opt.level";
    private static final int optimizationLevel = getOptimizationLevel();

    private static int getLanguageVersion() {
        int version;
        String tmp = AccessController.doPrivileged(
                new sun.security.action.GetPropertyAction(RHINO_JS_VERSION));
        if (tmp != null) {
            version = Integer.parseInt(tmp);
        } else {
            version = Context.VERSION_1_8;
        }
        return version;
    }

    private static int getOptimizationLevel() {
        int optLevel = -1;
        // disable optimizer under security manager, for now.
        if (System.getSecurityManager() == null) {
            optLevel = Integer.getInteger(RHINO_OPT_LEVEL, -1);
        }
        return optLevel;
    }

    /**
     * Create new Context instance to be associated with the current thread.
     */
    @Override
    protected Context makeContext() {
        Context cx = super.makeContext();
        cx.setLanguageVersion(DefaultContextFactory.languageVersion);
        cx.setOptimizationLevel(DefaultContextFactory.optimizationLevel);
        cx.setClassShutter(RhinoClassShutter.getInstance());
        cx.setWrapFactory(RhinoWrapFactory.getInstance());
        return cx;
    }

    /**
     * Execute top call to script or function. When the runtime is about to
     * execute a script or function that will create the first stack frame
     * with scriptable code, it calls this method to perform the real call.
     * In this way execution of any script happens inside this function.
     */
    @Override
    protected Object doTopCall(final Callable callable,
                               final Context cx, final Scriptable scope,
                               final Scriptable thisObj, final Object[] args) {
        AccessControlContext accCtxt = null;
        Scriptable global = ScriptableObject.getTopLevelScope(scope);
        Scriptable globalProto = global.getPrototype();
        if (globalProto instanceof RhinoTopLevel) {
            accCtxt = ((RhinoTopLevel) globalProto).getAccessContext();
        }

        if (accCtxt != null) {
            return AccessController.doPrivileged((PrivilegedAction<Object>) () ->
                    superDoTopCall(callable, cx, scope, thisObj, args), accCtxt);
        } else {
            return superDoTopCall(callable, cx, scope, thisObj, args);
        }
    }

    private Object superDoTopCall(Callable callable,
                                  Context cx, Scriptable scope,
                                  Scriptable thisObj, Object[] args) {
        return super.doTopCall(callable, cx, scope, thisObj, args);
    }
}
