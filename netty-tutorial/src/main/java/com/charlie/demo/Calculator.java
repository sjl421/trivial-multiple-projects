package com.charlie.demo;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * Created by dhy on 17-3-29.
 *
 */
public enum  Calculator {
    INSTANCE;

    private final static ScriptEngine jse = new ScriptEngineManager().getEngineByName("JavaScript");

    public Object cal(String expression) throws ScriptException {
        return jse.eval(expression);
    }
}
