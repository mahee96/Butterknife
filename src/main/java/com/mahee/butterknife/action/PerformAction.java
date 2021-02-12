package com.mahee.butterknife.action;

import com.intellij.codeInsight.CodeInsightActionHandler;
import com.intellij.codeInsight.generation.actions.BaseGenerateAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiUtilBase;
import com.intellij.psi.xml.XmlFile;
import com.mahee.butterknife.butterknife.ButterKnife;
import com.mahee.butterknife.butterknife.ButterKnifeFactory;
import com.mahee.butterknife.common.Constants;
import com.mahee.butterknife.common.Definitions;
import com.mahee.butterknife.data.Entity;
import com.mahee.butterknife.helper.LayoutHelper;
import com.mahee.butterknife.model.XmlElement;
import com.mahee.butterknife.ui.form.DialogBox;
import com.mahee.butterknife.ui.notification.Notification;
import com.mahee.butterknife.utils.Log;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

/**
 * Created by Magesh.K on 2/11/2021.
 */
public class PerformAction extends BaseGenerateAction
        implements Constants {

    private final String TAG = getClass().getSimpleName();
    private static final Log log = Log.getInstance(PerformAction.class);

    public PerformAction() { super(null); }

    protected PerformAction(CodeInsightActionHandler handler) { super(handler); }

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        final Project project = event.getProject();
        final Editor editor = event.getData(PlatformDataKeys.EDITOR);
        if(project == null || editor == null) return;
        actionPerformedImpl(project, editor);
    }

    @Override
    public void actionPerformedImpl(@NotNull Project project, final Editor editor) {
        final Entity entity = new Entity();
        entity.setProject(project);
        entity.setEditor(editor);

        final PsiFile currFile= PsiUtilBase.getPsiFileInEditor(editor, project);
        if(!(currFile instanceof PsiJavaFile))
            return;
        entity.setJavaFile((PsiJavaFile) currFile);

        final PsiFile layoutFile = LayoutHelper.fetchLayoutFile(editor, (PsiJavaFile) currFile);
        if (!(layoutFile instanceof XmlFile)) {
            Notification.showError(project, Messages.ERROR.NO_LAYOUT_FOUND);
            return;
        }
        entity.setRootLayoutWithFullPath(layoutFile.getVirtualFile().getPath());

        log.i(TAG,LogTag.INFO.LAYOUT_FILE + entity.getRootLayoutWithFullPath());

        entity.setFoundXmlElementIds(LayoutHelper.getIdsFromLayout(layoutFile));
        if (entity.getFoundXmlElementIds() == null) {
            Notification.showError(project, Messages.ERROR.NO_IDs_FOUND);
            return;
        }
        final PsiClass clazz = getTargetClass(editor, currFile);
        final ButterKnife butterKnife = ButterKnifeFactory.getForModule(project, currFile);
        if (clazz == null || butterKnife == null) return;
        entity.setJavaClass(getTargetClass(editor, currFile));
        entity.setButterknife(ButterKnifeFactory.getForModule(project, currFile));

        // get parent classes and check if it's an adapter
        setViewHolderOptions(entity);

        // get already generated injections
        validateExistingIds(entity);

        DialogBox.showDialog(entity);
    }

    private void validateExistingIds(final Entity entity) {
        final ArrayList<String> existingIds = new ArrayList<String>();
        final PsiField[] fields = entity.getJavaClass().getAllFields();
        String[] annotations;
        String foundId;

        for (PsiField field : fields) {
            annotations = field.getFirstChild().getText().split(" ");

            for (String annotation : annotations) {
                foundId = getBindId(entity.getButterknife(), annotation.trim());
                if (!StringUtils.isEmpty(foundId)) {
                    existingIds.add(foundId);
                }
            }
        }
        entity.setExistingStringIds(existingIds);
    }

    private void setViewHolderOptions(final Entity entity){
        final PsiReferenceList list = entity.getJavaClass().getExtendsList();
        if (list != null) {
            for (PsiJavaCodeReferenceElement element : list.getReferenceElements()) {
                if (Definitions.adapters.contains(element.getQualifiedName())) {
                    entity.setHolderSelection(true);
                }
            }
        }
    }

    public String getBindId(@NotNull final ButterKnife butterKnife, @NotNull final String annotation) {
        String id = null;
        if (StringUtils.isEmpty(annotation)) return id;

        Matcher matcher = butterKnife.getFieldAnnotationPattern().matcher(annotation);
        if (matcher.find()) id = matcher.group(1);
        return id;
    }
}
