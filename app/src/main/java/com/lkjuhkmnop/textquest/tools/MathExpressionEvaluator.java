package com.lkjuhkmnop.textquest.tools;

import android.util.Log;

import com.fathzer.soft.javaluator.DoubleEvaluator;
import com.fathzer.soft.javaluator.StaticVariableSet;

public class MathExpressionEvaluator {
    private final DoubleEvaluator doubleEvaluator = new DoubleEvaluator();
    private final StaticVariableSet<Double> variableSet = new StaticVariableSet<>();

    public Double evaluate(String ex) {
        Double result = 0.0;
        try {
            result = doubleEvaluator.evaluate(ex, variableSet);
        } catch (IllegalArgumentException e) {
            Log.e("play", e.getMessage());
        }
        return result;
    }

    public void setVariable(String name, double value) {
        variableSet.set(name, value);
    }
}
