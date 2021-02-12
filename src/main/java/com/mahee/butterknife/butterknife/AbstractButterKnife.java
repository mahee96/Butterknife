package com.mahee.butterknife.butterknife;

import java.util.regex.Pattern;

/**
 * @author mahee96
 */
public abstract class AbstractButterKnife implements ButterKnife {

    private static final String mPackageName = "butterknife";
    private static final String delim = ".";
    private final String mPackageNameWithDelim = mPackageName +delim;
    private final Pattern mFieldAnnotationPattern = Pattern.compile("^@" + getFieldAnnotationSimpleName() + "\\(([^\\)]+)\\)$", Pattern.CASE_INSENSITIVE);
    private final String mFieldAnnotationCanonicalName = mPackageNameWithDelim + getFieldAnnotationSimpleName();
    private final String mCanonicalBindStatement = mPackageNameWithDelim + getSimpleBindStatement();
    private final String mCanonicalUnbindStatement = mPackageNameWithDelim + getSimpleUnbindStatement();
    private final String mOnClickCanonicalName = mPackageNameWithDelim + "OnClick";
    private final String mUnbinderClassCanonicalName = mPackageNameWithDelim + getUnbinderClassSimpleName();


    @Override
    public Pattern getFieldAnnotationPattern() {
        return mFieldAnnotationPattern;
    }

    @Override
    public String getPackageName() {
        return mPackageName;
    }

    @Override
    public String getFieldAnnotationCanonicalName() {
        return mFieldAnnotationCanonicalName;
    }

    @Override
    public String getOnClickAnnotationCanonicalName() {
        return mOnClickCanonicalName;
    }

    @Override
    public String getCanonicalBindStatement() {
        return mCanonicalBindStatement;
    }

    @Override
    public boolean isUnbindSupported() {
        return true;
    }

    @Override
    public boolean isUsingUnbinder() {
        // Let's assume that this is going to stay after ButterKnife 8.
        return true;
    }

    @Override
    public String getCanonicalUnbindStatement() {
        return mCanonicalUnbindStatement;
    }

    @Override
    public String getUnbinderClassCanonicalName() {
        return mUnbinderClassCanonicalName;
    }
}
