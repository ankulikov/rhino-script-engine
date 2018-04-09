/*
 * Copyright (c) 2005, 2006, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package de.christophkraemer.rhino.javascript;

import org.mozilla.javascript.*;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.SimpleScriptContext;
import java.security.AccessControlContext;

/**
 * This class serves as top level scope for Rhino. This class adds two constructors
 * (JSAdapter, JavaAdapter).
 *
 * @author (Original) A. Sundararajan
 */
public final class RhinoTopLevel extends ImporterTopLevel {
    private RhinoScriptEngine engine;

    RhinoTopLevel(Context cx, RhinoScriptEngine engine) {
        // second boolean parameter to super constructor tells whether
        // to seal standard JavaScript objects or not. If security manager
        // is present, we seal the standard objects.
        super(cx, System.getSecurityManager() != null);
        this.engine = engine;

        // initialize JSAdapter lazily. Reduces footprint & startup time.
        new LazilyLoadedCtor(this, "JSAdapter",
                "com.sun.script.javascript.JSAdapter",
                false);

        /*
         * initialize JavaAdapter. We can't lazy initialize this because
         * lazy initializer attempts to define a new property. But, JavaAdapter
         * is an exisiting property that we overwrite.
         */
        JavaAdapter.init(cx, this, false);
    }

    RhinoScriptEngine getScriptEngine() {
        return engine;
    }

    AccessControlContext getAccessContext() {
        return engine.getAccessContext();
    }
}