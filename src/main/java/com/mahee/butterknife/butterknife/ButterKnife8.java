package com.mahee.butterknife.butterknife;

/**
 * ButterKnife version 8
 *
 * @author mahee96
 */
public class ButterKnife8 extends com.mahee.butterknife.butterknife.AbstractButterKnife {

    private static final String mFieldAnnotationSimpleName = "BindView";
    private static final String mSimpleBindStatement = "ButterKnife.bind";
    private static final String mSimpleUnbindStatement = ".unbind";
    private static final String mUnbinderClassSimpleName = "Unbinder";

    @Override
    public String getVersion() {
        return "8.0.1";
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
    public String getCanonicalUnbindStatement() {
        return getSimpleUnbindStatement();
    }

    @Override
    public String getUnbinderClassSimpleName() {
        return mUnbinderClassSimpleName;
    }
}
