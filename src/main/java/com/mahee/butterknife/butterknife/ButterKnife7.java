package com.mahee.butterknife.butterknife;

/**
 * ButterKnife version 7
 *
 * @author mahee96
 */
public class ButterKnife7 extends AbstractButterKnife {

    private static final String mFieldAnnotationSimpleName = "Bind";
    private static final String mSimpleBindStatement = "ButterKnife.bind";
    private static final String mSimpleUnbindStatement = "ButterKnife.unbind";

    @Override
    public String getVersion() {
        return "7.0.1";
    }

    @Override
    public String getDistinctClassName() {
        return getFieldAnnotationCanonicalName();
    }

    @Override
    public String getFieldAnnotationSimpleName() {
        return mFieldAnnotationSimpleName;
    }

    @Override
    public String getSimpleBindStatement() {
        return mSimpleBindStatement;
    }

    @Override
    public String getSimpleUnbindStatement() {
        return mSimpleUnbindStatement;
    }

    @Override
    public boolean isUsingUnbinder() {
        return false;
    }

    @Override
    public String getUnbinderClassSimpleName() {
        return null;
    }
}
