package com.mahee.butterknife.butterknife;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Factory for obtaining proper ButterKnife version.
 *
 * @author mahee96
 */
public class ButterKnifeFactory {

    /**
     * List of supported ButterKnifes.
     * Note: The ordering corresponds to the preferred ButterKnife versions.
     */
    private static final ButterKnife[] sSupportedButterKnives = new ButterKnife[]{
            new ButterKnife8(),
            new ButterKnife7(),
            new ButterKnife6(),
    };

    // No construction
    private ButterKnifeFactory() {}

    /**
     * Finds if supported ButterKnife is available in the classpath of requesting element's module
     * If not found, falls back to find in whole project, if still not found,
     * ButterKnife dependency is missing or not intended for current project.
     *
     * @param project    Project instance {@link Project}
     * @param editorFile current element for which we require butterknife binding {@link PsiElement}
     * @return ButterKnife supported ButterKnife versions {@link com.mahee.butterknife.butterknife.ButterKnife}
     */
    @Nullable
    public static ButterKnife getForModule(@NotNull Project project,
                                                                                            @NotNull PsiElement editorFile) {
        for (ButterKnife butterKnife : sSupportedButterKnives) {
            if (classInModuleClasspath(project, editorFile, butterKnife.getDistinctClassName()))
                return butterKnife;
        }
        return getForProject(project);
    }

    /**
     * Finds if supported ButterKnife is available in the classpath of {@link Project}
     *
     * @param project Project instance {@link Project}
     * @return ButterKnife supported ButterKnife versions {@link com.mahee.butterknife.butterknife.ButterKnife} else null
     */
    @Nullable
    private static ButterKnife getForProject(@NotNull Project project) {
        for (ButterKnife butterKnife : sSupportedButterKnives) {
            if (classInProjectClasspath(project, butterKnife.getDistinctClassName()))
                return butterKnife;
        }
        return null;
    }

    public static ButterKnife[] getSupportedButterKnives() {
        return sSupportedButterKnives;
    }

    /**
     * Check whether classpath of a module that corresponds to a {@link PsiElement} contains given class.
     *
     * @param project    Project
     * @param psiElement Element for which we check the class
     * @param className  Class name of the searched class
     * @return True if the class is present on the classpath
     */
    public static boolean classInModuleClasspath(@NotNull Project project,
                                                 @NotNull PsiElement psiElement,
                                                 @NotNull String className) {
        Module module = ModuleUtil.findModuleForPsiElement(psiElement);
        if (module == null)
            return false;
        final GlobalSearchScope moduleScope = module.getModuleWithDependenciesAndLibrariesScope(false);
        return JavaPsiFacade
                .getInstance(project)
                .findClass(className, moduleScope) != null;
    }

    /**
     * Check whether classpath of a the whole project contains given class.
     * This is only fallback for wrongly setup projects.
     *
     * @param project   Project
     * @param className Class name of the searched class
     * @return True if the class is present on the classpath
     */
    public static boolean classInProjectClasspath(@NotNull Project project,
                                                  @NotNull String className) {
        return JavaPsiFacade
                .getInstance(project)
                .findClass(className, GlobalSearchScope.everythingScope(project)) != null;
    }
}
