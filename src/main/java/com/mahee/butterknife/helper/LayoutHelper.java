package com.mahee.butterknife.helper;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.mahee.butterknife.common.Constants;
import com.mahee.butterknife.model.XmlElement;
import com.mahee.butterknife.parser.XmlParser;
import com.mahee.butterknife.utils.Log;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Magesh.K on 2/11/2021.
 */
public class LayoutHelper implements Constants {
    private static final Log log = Log.getInstance(LayoutHelper.class);
    private static final String TAG = LayoutHelper.class.getSimpleName();

    public static PsiFile fetchLayoutFile(Editor editor, PsiJavaFile file) {
        int offset = editor.getCaretModel().getOffset();

        PsiElement candidateA = file.findElementAt(offset);
        PsiElement candidateB = file.findElementAt(offset - 1);

        PsiFile layout = resolveLayoutFile(candidateA);
        if (layout != null) {
            return layout;
        }
        return resolveLayoutFile(candidateB);
    }

    public static PsiFile resolveLayoutFile(@Nullable PsiElement element) {
        if (!(element instanceof PsiIdentifier)) return null;
        log.i(TAG, "Finding layout resource for element: " + element.getText());

        final PsiElement layout = element.getParent().getFirstChild();
        if (layout == null) return null; // no file to process
        if (!layout.getText().equals(JavaParserTag.LAYOUT)) return null; // not layout file }

        final Project project = element.getProject();
        final String name = String.format("%s.xml", element.getText());
        return resolveLayoutResourceFile(element, project, name);
    }

    public static PsiFile resolveLayoutFile(PsiFile file, Project project, String fileName) {
        String name = String.format("%s.xml", fileName);
        // restricting the search to the module of layout that includes the layout we are seaching for
        return resolveLayoutResourceFile(file, project, name);
    }

    private static PsiFile resolveLayoutResourceFile(PsiElement element, Project project, String name) {
        // restricting the search to the current module - searching the whole project could return wrong layouts
        final Module module = ModuleUtil.findModuleForPsiElement(element);
        PsiFile[] files = null;
        if (module != null) {
            // first omit libraries, it might cause issues like (#103)
            GlobalSearchScope moduleScope = module.getModuleWithDependenciesScope();
            files = FilenameIndex.getFilesByName(project, name, moduleScope);
            if (files == null || files.length <= 0) {
                // now let's do a fallback including the libraries
                moduleScope = module.getModuleWithDependenciesAndLibrariesScope(false);
                files = FilenameIndex.getFilesByName(project, name, moduleScope);
            }
        }
        if (files == null || files.length <= 0) {
            // fallback to search through the whole project
            // useful when the project is not properly configured - when the resource directory is not configured
            files = FilenameIndex.getFilesByName(project, name, GlobalSearchScope.everythingScope(project));
            if (files.length <= 0) {
                return null; //no matching files
            }
        }

        // TODO - we have a problem here - we still can have multiple layouts (some coming from a dependency)
        // we need to resolve R class properly and find the proper layout for the R class
        for (PsiFile file : files) {
            log.i(TAG,"Resolved layout resource file for name [" + name + "]: " + file.getVirtualFile());
        }
        return files[0];
    }

    public static List<XmlElement> getIdsFromLayout(final PsiFile file) {
        return getIdsFromLayout(file, null);
    }

    public static List<XmlElement> getIdsFromLayout(@NotNull final PsiFile layoutFile,
                                                    @Nullable List<XmlElement> xmlElements) {
        final List<XmlElement> newList = new ArrayList<>();
        layoutFile.accept(new XmlParser(layoutFile, newList));
        if (xmlElements != null) {
            xmlElements.addAll(newList);
        } else{
            xmlElements = newList;
        }
        return xmlElements.size() > 0 ? xmlElements: null;
    }
}
