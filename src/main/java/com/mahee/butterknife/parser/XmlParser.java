package com.mahee.butterknife.parser;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.XmlRecursiveElementVisitor;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlTag;
import com.mahee.butterknife.common.Constants;
import com.mahee.butterknife.helper.LayoutHelper;
import com.mahee.butterknife.model.XmlElement;
import com.mahee.butterknife.utils.Log;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created by Magesh.K on 2/11/2021.
 */
public class XmlParser extends XmlRecursiveElementVisitor implements Constants {

    private static final Log log = Log.getInstance(XmlParser.class);
    private final static String TAG = XmlParser.class.getSimpleName();

    private Project mProject;
    private PsiFile mLayoutFile;
    private String mXmlTagName;
    private String mIdValue;
    private XmlTag mXmlTag;
    private List<XmlElement> mXmlElements;

    private XmlParser() { }

    public XmlParser(@NotNull PsiFile layoutFile, @NotNull List<XmlElement> xmlElements) {
        this.mLayoutFile = layoutFile;
        this.mXmlElements = xmlElements;
        this.mProject = mLayoutFile.getProject();
    }

    @Override
    public void visitElement(@NotNull final PsiElement element) {
        super.visitElement(element);
        // is Xml Tag?
        if (element instanceof XmlTag) {
            this.mXmlTag = (XmlTag) element;
            this.mXmlTagName = mXmlTag.getName();
            // is Xml Tag == <include>?
            if (mXmlTagName.equalsIgnoreCase(XmlParserTag.INCLUDE)) {
                processIncludeTag();
            } else {
                processTag();
            }
        }
    }

    private void processIncludeTag() {
        final XmlAttribute layout = mXmlTag.getAttribute(XmlParserTag.Attribute.LAYOUT, null);
        // if full tag is <include layout="...">
        if (layout != null) recursiveLayoutParse(layout);
    }

    private void recursiveLayoutParse(final XmlAttribute attrLayout) {
        final PsiFile includeLayoutFile = LayoutHelper.resolveLayoutFile(mLayoutFile, mProject, parseLayoutName(attrLayout.getValue()));
        if (includeLayoutFile == null) return;
        // perform recursive parsing
        includeLayoutFile.accept(new XmlParser(includeLayoutFile, mXmlElements));
    }

    private void processTag() {
        // obtain custom class name if available from tag attributes
        final XmlAttribute clazz = mXmlTag.getAttribute(XmlParserTag.Attribute.CLASS, null);
        if (clazz != null) mXmlTagName = clazz.getValue();

        final XmlAttribute id = mXmlTag.getAttribute(XmlParserTag.Attribute.ID, null);
        // if android:id=id.value is available
        if (id != null) {
            mIdValue = id.getValue();
            if (mIdValue != null) {
                try {
                    mXmlElements.add(new XmlElement(mXmlTagName, mIdValue));
                    log.i(TAG, "Successfully Parsed <" + mXmlTagName + "> "
                            + "whose "+XmlParserTag.Attribute.ID+" attribute value is: "
                            + mIdValue);
                } catch (IllegalArgumentException e) {
                    log.e(TAG, e.getClass().getSimpleName()
                            + " while trying to add XmlTag<" + mXmlTagName + ">", e);
                }
            } else {
                log.e(TAG, "Value for Attribute \"android:id\" not found in XmlTag<" + mXmlTagName + ">");
            }
        } else {
            log.e(TAG, "Attribute \"android:id\" not found for XmlTag<" + mXmlTagName + ">");
        }
    }

    public static String parseLayoutName(final String layoutFullName) {
        // layout string should be of format of the format "@layout/filename"
        if (layoutFullName == null || !layoutFullName.startsWith("@") || !layoutFullName.contains("/"))
            return null;

        final String[] parts = layoutFullName.split("/");
        if (parts.length != 2) return null;
        return parts[1];
    }
}
