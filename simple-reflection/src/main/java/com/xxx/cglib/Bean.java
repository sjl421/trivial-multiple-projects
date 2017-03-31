package com.xxx.cglib;

import java.beans.PropertyChangeListener;

/**
 * Created by dhy on 17-3-31.
 *
 */
public abstract class Bean implements java.io.Serializable {
    String sampleProperty;

    abstract public void addPropertyChangeListener(PropertyChangeListener listener);

    abstract public void removePropertyChangeListener(PropertyChangeListener listener);

    public String getSampleProperty() {
        return sampleProperty;
    }

    public void setSampleProperty(String sampleProperty) {
        this.sampleProperty = sampleProperty;
    }

    @Override
    public String toString() {
        return "sampleProperty is " + sampleProperty;
    }
}
