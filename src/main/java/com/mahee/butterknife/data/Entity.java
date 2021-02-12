package com.mahee.butterknife.data;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.xml.XmlFile;
import com.mahee.butterknife.butterknife.ButterKnife;
import com.mahee.butterknife.model.XmlElement;

import java.util.List;

/**
 * Created by Magesh.K on 2/12/2021.
 */
public class Entity {
    private ButterKnife mButterknife;
    private Project mProject;
    private Editor mEditor;
    private PsiJavaFile mJavaFile;
    private PsiClass mJavaClass;
    private XmlFile mRootLayoutFile;
    private String mRootLayoutCanonicalName;
    private String mRootLayoutSimpleName;
    private String mRootLayoutWithFullPath;
    private boolean mHolderSelection = false;
    private boolean mSplitOnClickMethods = false;
    private String mVariableNamePrefix;
    private List<XmlElement> mFoundXmlElementIds;

    public String getRootLayoutWithFullPath() {
        return mRootLayoutWithFullPath;
    }

    public void setRootLayoutWithFullPath(String mRootLayoutWithFullPath) {
        this.mRootLayoutWithFullPath = mRootLayoutWithFullPath;
    }

    private List<String> mExistingStringIds;


    public Project getProject() {
        return mProject;
    }

    public void setProject(Project mProject) {
        this.mProject = mProject;
    }

    public PsiJavaFile getJavaFile() {
        return mJavaFile;
    }

    public void setJavaFile(PsiJavaFile mJavaFile) {
        this.mJavaFile = mJavaFile;
    }

    public XmlFile getRootLayoutFile() {
        return mRootLayoutFile;
    }

    public void setRootLayoutFile(XmlFile mRootLayoutFile) {
        this.mRootLayoutFile = mRootLayoutFile;
    }

    public String getRootLayoutCanonicalName() {
        return mRootLayoutCanonicalName;
    }

    public void setRootLayoutCanonicalName(String mRootLayoutCanonicalName) {
        this.mRootLayoutCanonicalName = mRootLayoutCanonicalName;
    }

    public String getRootLayoutSimpleName() {
        return mRootLayoutSimpleName;
    }

    public void setRootLayoutSimpleName(String mRootLayoutSimpleName) {
        this.mRootLayoutSimpleName = mRootLayoutSimpleName;
    }

    public boolean isHolderSelection() {
        return mHolderSelection;
    }

    public void setHolderSelection(boolean mHolderSelection) {
        this.mHolderSelection = mHolderSelection;
    }

    public boolean isSplitOnClickMethods() {
        return mSplitOnClickMethods;
    }

    public void setSplitOnClickMethods(boolean mSplitOnClickMethods) {
        this.mSplitOnClickMethods = mSplitOnClickMethods;
    }

    public String getVariableNamePrefix() {
        return mVariableNamePrefix;
    }

    public void setVariableNamePrefix(String mVariableNamePrefix) {
        this.mVariableNamePrefix = mVariableNamePrefix;
    }

    public List<XmlElement> getFoundXmlElementIds() {
        return mFoundXmlElementIds;
    }

    public void setFoundXmlElementIds(List<XmlElement> mFoundXmlElementIds) {
        this.mFoundXmlElementIds = mFoundXmlElementIds;
    }

    public List<String> getExistingStringIds() {
        return mExistingStringIds;
    }

    public void setExistingStringIds(List<String> mExistingIds) {
        this.mExistingStringIds = mExistingIds;
    }

    public PsiClass getJavaClass() {
        return mJavaClass;
    }

    public void setJavaClass(PsiClass mJavaClass) {
        this.mJavaClass = mJavaClass;
    }

    public ButterKnife getButterknife() {
        return mButterknife;
    }

    public void setButterknife(ButterKnife mbutterknife) {
        this.mButterknife = mbutterknife;
    }

    public Editor getEditor() {
        return mEditor;
    }

    public void setEditor(Editor mEditor) {
        this.mEditor = mEditor;
    }
}
