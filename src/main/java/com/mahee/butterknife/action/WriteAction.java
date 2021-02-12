package com.mahee.butterknife.action;

import com.intellij.application.options.CodeStyle;
import com.intellij.codeInsight.actions.ReformatCodeProcessor;
import com.intellij.formatting.FormatTextRanges;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.application.Result;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import com.intellij.psi.impl.source.codeStyle.CodeFormatterFacade;
import com.intellij.psi.search.GlobalSearchScope;
import com.mahee.butterknife.butterknife.ButterKnife;
import com.mahee.butterknife.butterknife.ButterKnifeFactory;
import com.mahee.butterknife.common.Constants;
import com.mahee.butterknife.common.Definitions;
import com.mahee.butterknife.data.Entity;
import com.mahee.butterknife.model.XmlElement;
import com.mahee.butterknife.ui.form.Settings;
import com.mahee.butterknife.ui.notification.Notification;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class WriteAction extends WriteCommandAction implements Constants {

    private static WriteAction writeAction;
    private Entity mEntity;
    private PsiElementFactory mFactory;

    private WriteAction(Project project, String command) {
        super(project, command);
    }

    public static int getInjectCount(List<XmlElement> elements) {
        return getCount(elements);
    }

    public static int getClickCount(List<XmlElement> elements) {
        return getCount(elements);
    }

    public static int getCount(List<XmlElement> elements) {
        int cnt = 0;
        for (XmlElement element : elements) {
            if (element.used) cnt++;
        }
        return cnt;
    }

    public static WriteAction Builder(final Entity entity, String command) {
        if (writeAction == null) writeAction = new WriteAction(entity.getProject(), command);
        writeAction.mEntity = entity;
        writeAction.mFactory = JavaPsiFacade.getElementFactory(entity.getProject());
        return writeAction;
    }
    
    private void generateClick() {
        if (WriteAction.getClickCount(mEntity.getFoundXmlElementIds()) == 1) {
            generateSingleClickMethod();
        } else {
            if (mEntity.isSplitOnClickMethods()) {
                generateMultipleClickMethods();
            } else {
                generateSingleClickMethodForSeveralIds();
            }
        }
    }

    private void generateSingleClickMethod() {
        StringBuilder method = new StringBuilder();
        method.append("@OnClick((");
        for (XmlElement viewElement : mEntity.getFoundXmlElementIds()) {
            if (viewElement.isClick) {
                method.append(viewElement.getFullID() + ")");
            }
        }
        method.append("public void onViewClicked() {}");
        mEntity.getJavaClass().add(mFactory.createMethodFromText(method.toString(), mEntity.getJavaClass()));
    }

    private void generateMultipleClickMethods() {
        for (XmlElement viewElement : mEntity.getFoundXmlElementIds()) {
            if (viewElement.isClick) {
                StringBuilder method = new StringBuilder();
                method.append("@OnClick((" + viewElement.getFullID() + ")");
                method.append("public void on" + StringUtils.capitalize(viewElement.fieldName) + "Clicked() {}");
                mEntity.getJavaClass().add(mFactory.createMethodFromText(method.toString(), mEntity.getJavaClass()));
            }
        }
    }

    private void generateSingleClickMethodForSeveralIds() {
        StringBuilder method = new StringBuilder();
        method.append("@OnClick(({");
        int currentCount = 0;
        for (XmlElement viewElement : mEntity.getFoundXmlElementIds()) {
            if (viewElement.isClick) {
                currentCount++;
                if (currentCount == WriteAction.getClickCount(mEntity.getFoundXmlElementIds())) {
                    method.append(viewElement.getFullID() + "})");
                } else {
                    method.append(viewElement.getFullID() + ",");
                }
            }
        }
        method.append("public void onViewClicked(android.view.View view) {switch (view.getId()){");
        for (XmlElement viewElement : mEntity.getFoundXmlElementIds()) {
            if (viewElement.isClick) {
                method.append("case " + viewElement.getFullID() + ": break;");
            }
        }
        method.append("}}");
        mEntity.getJavaClass().add(mFactory.createMethodFromText(method.toString(), mEntity.getJavaClass()));
    }

    /**
     * Create ViewHolder for adapters with injections
     */
    private void generateAdapter(@NotNull ButterKnife butterKnife) {
        final PsiClass javaClass = mEntity.getJavaClass();
        String holderBuilder = getViewHolderClassName() +
                "(android.view.View view) {" +
//                butterKnife.getCanonicalBindStatement() +
                butterKnife.getSimpleBindStatement() +
                "(this, view);" +
                "}";
        PsiClass viewHolder = mFactory.createClassFromText(holderBuilder, javaClass);
        viewHolder.setName(getViewHolderClassName());

        // add injections into view holder
        for (XmlElement viewElement : mEntity.getFoundXmlElementIds()) {
            if (!viewElement.used) {
                continue;
            }

            String rPrefix  = "R.id.";
            if (viewElement.isAndroidNS)
                rPrefix = "android.R.id.";

            StringBuilder injection = new StringBuilder();
            injection.append('@');
//            injection.append(butterKnife.getFieldAnnotationCanonicalName());
            injection.append(butterKnife.getFieldAnnotationSimpleName());
            injection.append('(');
            injection.append(rPrefix);
            injection.append(viewElement.id);
            injection.append(") ");
            if (viewElement.nameFull != null && viewElement.nameFull.length() > 0) { // custom package+class
                injection.append(viewElement.nameFull);
            } else if (Definitions.paths.containsKey(viewElement.name)) { // listed class
                injection.append(Definitions.paths.get(viewElement.name));
            } else { // android.widget
                injection.append("android.widget.");
                injection.append(viewElement.name);
            }
            injection.append(" ");
            injection.append(viewElement.fieldName);
            injection.append(";");

            viewHolder.add(mFactory.createFieldFromText(injection.toString(), javaClass));
        }

        javaClass.add(viewHolder);

        // add view holder's comment
        StringBuilder comment = new StringBuilder();
        comment.append("/**\n");
        comment.append(" * This class contains all butterknife-injected Views & Layouts from layout file '");
        comment.append(mEntity.getRootLayoutSimpleName());
        comment.append("'\n");
        comment.append("* for easy to all layout elements.\n");
        comment.append(" *\n");
        comment.append(" * @author\tmahee96\n");
        comment.append("*/");

        javaClass.addBefore(mFactory.createCommentFromText(comment.toString(), javaClass), javaClass.findInnerClassByName(getViewHolderClassName(), true));
        javaClass.addBefore(mFactory.createKeyword("static", javaClass), javaClass.findInnerClassByName(getViewHolderClassName(), true));
    }

    /**
     * Create fields for injections inside main class
     */
    private void generateFields(@NotNull ButterKnife butterKnife) {
        // add injections into main class
        for (XmlElement viewElement : mEntity.getFoundXmlElementIds()) {
            if (!viewElement.used) {
                continue;
            }

            StringBuilder injection = new StringBuilder();
            injection.append('@');
//            injection.append(butterKnife.getFieldAnnotationCanonicalName());
            injection.append(butterKnife.getFieldAnnotationSimpleName());
            injection.append('(');
            injection.append(viewElement.getFullID());
            injection.append(") ");
            if (viewElement.nameFull != null && viewElement.nameFull.length() > 0) { // custom package+class
                injection.append(viewElement.nameFull);
            } else if (Definitions.paths.containsKey(viewElement.name)) { // listed class
                injection.append(Definitions.paths.get(viewElement.name));
            } else { // android.widget
                injection.append("android.widget.");
                injection.append(viewElement.name);
            }
            injection.append(" ");
            injection.append(viewElement.fieldName);
            injection.append(";");

            mEntity.getJavaClass().add(mFactory.createFieldFromText(injection.toString(), mEntity.getJavaClass()));
        }
    }

    /**
     * Validates whether ButterKnife "Inject" line is annotated for the current method
     *
     * @param method The method whose annotation is being validated
     * @param line The line (Text) which is being parsed for validation
     * @return
     */
    private boolean containsButterKnifeInjectLine(PsiMethod method, String line) {
        final PsiCodeBlock body = method.getBody();
        if (body == null) {
            return false;
        }
        PsiStatement[] statements = body.getStatements();
        for (PsiStatement psiStatement : statements) {
            String statementAsString = psiStatement.getText();
            if (psiStatement instanceof PsiExpressionStatement
                    && (statementAsString.contains(line))) {
                return true;
            }
        }
        return false;
    }

    private void generateInjects(@NotNull ButterKnife butterKnife) {
        final String[] activities = {
                "android.app.Activity",
                "androidx.appcompat.app.AppCompatActivity",
        };

        final String[] fragments = {
                "android.app.Fragment",
                "androidx.fragment.app",
        };

        PsiClass activityClass = null;
        PsiClass fragmentClass = null;

        for(String activity: activities){
            activityClass = JavaPsiFacade.getInstance(getProject()).findClass(
                    activity, GlobalSearchScope.everythingScope(getProject()));
        }

        for(String fragment: fragments){
            fragmentClass = JavaPsiFacade.getInstance(getProject()).findClass(
                    fragment, GlobalSearchScope.everythingScope(getProject()));
        }

        for(String fragment: activities){
            fragmentClass = JavaPsiFacade.getInstance(getProject()).findClass(
                    fragment, GlobalSearchScope.everythingScope(getProject()));
        }
        // Check for Activity class
        if (activityClass != null && mEntity.getJavaClass().isInheritor(activityClass, true)) {
            generateActivityBind(butterKnife);
        // Check for Fragment class
        } else if ((fragmentClass != null && mEntity.getJavaClass().isInheritor(fragmentClass, true))) {
            generateFragmentBindAndUnbind(butterKnife);
        }
    }

    public static boolean hasImportClazz(@NotNull PsiJavaFile file, @NotNull String clazzName) {
        PsiImportList importList = file.getImportList();
        if (null == importList) {
            return false;
        }
        PsiImportStatement[] statements = importList.getImportStatements();
        for (PsiImportStatement tmp : statements) {
            if (null != tmp && tmp.getQualifiedName().equals(clazzName)) {
                return true;
            }
        }
        return false;
    }

    public void addMissingImports(PsiClass butterknifeClass) {
       final PsiImportList importList = mEntity.getJavaFile().getImportList();

        //TODO: NOTIFY no imports found! - is this a valid Android File?
        if(importList == null) return;

        boolean hasImport = false;
        for(final PsiImportStatement importLine: mEntity.getJavaFile().getImportList().getImportStatements()){
            final String importClassName = importLine.getQualifiedName();
            if(importClassName != null && importClassName.equals(butterknifeClass.getQualifiedName())){
                hasImport = true;
            }
        }
        if (hasImport) return;  // import already added? return

        PsiImportStatement statement = mFactory.createImportStatement(butterknifeClass);
        importList.add(statement);

        JavaCodeStyleManager.getInstance(getProject())
                .addImport((PsiJavaFile) mEntity.getJavaClass().getContainingFile(), butterknifeClass);
    }

    private void generateActivityBind(@NotNull ButterKnife butterKnife) {
        if (mEntity.getJavaClass().findMethodsByName("onCreate", false).length == 0) {
            // Add an empty stub of onCreate()
            StringBuilder method = new StringBuilder();
            method.append("@Override private void onCreate(android.os.Bundle savedInstanceState) {\n");
            method.append("super.onCreate(savedInstanceState);\n");
            method.append("\t// TODO: add setContentView(...) invocation\n");
//            method.append(butterKnife.getCanonicalBindStatement());
            method.append(butterKnife.getSimpleBindStatement());
            method.append("(this);\n");
            method.append("}");

            mEntity.getJavaClass().add(mFactory.createMethodFromText(method.toString(), mEntity.getJavaClass()));
        } else {
            PsiMethod onCreate = mEntity.getJavaClass().findMethodsByName("onCreate", false)[0];
            if (!containsButterKnifeInjectLine(onCreate, butterKnife.getSimpleBindStatement())) {
                for (PsiStatement statement : onCreate.getBody().getStatements()) {
                    // Search for setContentView()
                    if (statement.getFirstChild() instanceof PsiMethodCallExpression) {
                        PsiReferenceExpression methodExpression
                            = ((PsiMethodCallExpression) statement.getFirstChild())
                            .getMethodExpression();
                        // Insert ButterKnife.inject()/ButterKnife.bind() after setContentView()
                        if (methodExpression.getText().equals("setContentView")) {
                            onCreate.getBody().addAfter(mFactory.createStatementFromText(
//                                butterKnife.getCanonicalBindStatement() + "(this);", mEntity.getJavaClass()), statement);
                                butterKnife.getSimpleBindStatement() + "(this);", mEntity.getJavaClass()), statement);
                            break;
                        }
                    }
                }
            }
        }
    }

    private void generateFragmentBindAndUnbind(@NotNull ButterKnife butterKnife) {
        boolean generateUnbinder = false;
        String unbinderName = null;
        if (butterKnife.isUsingUnbinder()) {
            unbinderName = getNameForUnbinder(butterKnife);
        }

        // onCreateView() doesn't exist, let's create it
        if (mEntity.getJavaClass().findMethodsByName("onCreateView", false).length == 0) {
            // Add an empty stub of onCreateView()
            StringBuilder method = new StringBuilder();
            method.append("@Override public View onCreateView(android.view.LayoutInflater inflater, android.view.ViewGroup container, android.os.Bundle savedInstanceState) {\n");
            method.append("\t// TODO: inflate a fragment view\n");
            method.append("android.view.View rootView = super.onCreateView(inflater, container, savedInstanceState);\n");
            if (butterKnife.isUsingUnbinder()) {
                method.append(unbinderName);
                method.append(" = ");
            }
//            method.append(butterKnife.getCanonicalBindStatement());
            method.append(butterKnife.getSimpleBindStatement());
            method.append("(this, rootView);\n");
            method.append("return rootView;\n");
            method.append("}");

            mEntity.getJavaClass().add(mFactory.createMethodFromText(method.toString(), mEntity.getJavaClass()));
        } else {
            // onCreateView() exists, let's update it with an inject/bind statement
            PsiMethod onCreateView = mEntity.getJavaClass().findMethodsByName("onCreateView", false)[0];
            if (!containsButterKnifeInjectLine(onCreateView, butterKnife.getSimpleBindStatement())) {
                for (PsiStatement statement : onCreateView.getBody().getStatements()) {
                    if (statement instanceof PsiReturnStatement) {
                        String returnValue = ((PsiReturnStatement) statement).getReturnValue().getText();
                        // there's layout inflatiion
                        if (returnValue.contains("R.layout")) {
                            onCreateView.getBody().addBefore(mFactory.createStatementFromText("android.view.View view = " + returnValue + ";", mEntity.getJavaClass()), statement);
                            StringBuilder bindText = new StringBuilder();
                            if (butterKnife.isUsingUnbinder()) {
                                bindText.append(unbinderName);
                                bindText.append(" = ");
                            }
//                            bindText.append(butterKnife.getCanonicalBindStatement());
                            bindText.append(butterKnife.getSimpleBindStatement());
                            bindText.append("(this, view);");
                            PsiStatement bindStatement = mFactory.createStatementFromText(bindText.toString(), mEntity.getJavaClass());
                            onCreateView.getBody().addBefore(bindStatement, statement);
                            statement.replace(mFactory.createStatementFromText("return view;", mEntity.getJavaClass()));
                        } else {
                            // Insert ButterKnife.inject()/ButterKnife.bind() before returning a view for a fragment
                            StringBuilder bindText = new StringBuilder();
                            if (butterKnife.isUsingUnbinder()) {
                                bindText.append(unbinderName);
                                bindText.append(" = ");
                            }
//                            bindText.append(butterKnife.getCanonicalBindStatement());
                            bindText.append(butterKnife.getSimpleBindStatement());
                            bindText.append("(this, ");
                            bindText.append(returnValue);
                            bindText.append(");");
                            PsiStatement bindStatement = mFactory.createStatementFromText(bindText.toString(), mEntity.getJavaClass());
                            onCreateView.getBody().addBefore(bindStatement, statement);
                        }
                        break;
                    }
                }
            }
        }

        // Insert ButterKnife.reset(this)/ButterKnife.unbind(this)/unbinder.unbind()
        if (butterKnife.isUnbindSupported()) {
            // Create onDestroyView method if it's missing
            if (mEntity.getJavaClass().findMethodsByName("onDestroyView", false).length == 0) {
                StringBuilder method = new StringBuilder();
                method.append("@Override public void onDestroyView() {\n");
                method.append("super.onDestroyView();\n");
                method.append(generateUnbindStatement(butterKnife, unbinderName, true));
                method.append("}");

                mEntity.getJavaClass().add(mFactory.createMethodFromText(method.toString(), mEntity.getJavaClass()));
            } else {
                // there's already onDestroyView(), let's add the unbind statement
                PsiMethod onDestroyView = mEntity.getJavaClass().findMethodsByName("onDestroyView", false)[0];
                if (!containsButterKnifeInjectLine(onDestroyView, butterKnife.getSimpleUnbindStatement())) {
                    StringBuilder unbindText = generateUnbindStatement(butterKnife, unbinderName, false);
                    final PsiStatement unbindStatement = mFactory.createStatementFromText(unbindText.toString(), mEntity.getJavaClass());
                    onDestroyView.getBody().addBefore(unbindStatement, onDestroyView.getBody().getLastBodyElement());
                }
            }
        }

        // create unbinder field if necessary
        if (butterKnife.isUsingUnbinder() && (mEntity.getJavaClass().findFieldByName(unbinderName, false) == null)) {
            String unbinderFieldText = butterKnife.getUnbinderClassCanonicalName() + " " + unbinderName + ";";
            mEntity.getJavaClass().add(mFactory.createFieldFromText(unbinderFieldText, mEntity.getJavaClass()));
        }
    }

    private static StringBuilder generateUnbindStatement(@NotNull ButterKnife butterKnife,
                                                         String unbinderName,
                                                         boolean partOfMethod) {
        StringBuilder unbindText = new StringBuilder();
        if (butterKnife.isUsingUnbinder()) {
            unbindText.append(unbinderName);
            unbindText.append(butterKnife.getSimpleUnbindStatement());
            unbindText.append("();");
            if (partOfMethod) {
                unbindText.append('\n');
            }
        } else {
            unbindText.append(butterKnife.getCanonicalUnbindStatement());
            unbindText.append("(this);");
            if (partOfMethod) {
                unbindText.append('\n');
            }
        }
        return unbindText;
    }

    /**
     * Generate unique name for the unbinder.
     *
     * @param butterKnife Version of the ButterKnife.
     * @return Name for the unbinder variable.
     */
    private String getNameForUnbinder(@NotNull ButterKnife butterKnife) {
        // first, look for existing unbinder
        for (PsiField field : mEntity.getJavaClass().getFields()) {
            if (field.getType().getCanonicalText().equals(butterKnife.getUnbinderClassCanonicalName())) {
                return field.getNameIdentifier().getText();
            }
        }
        // find available name for unbinder field
        String unbinderName = "unbinder";
        int idx = 1;
        while (mEntity.getJavaClass().findFieldByName(unbinderName, false) != null) {
            unbinderName = "unbinder" + idx++;
        }
        return unbinderName;
    }

    @Override
    public void run(@NotNull Result result) {
        final PsiJavaFile javaFile = mEntity.getJavaFile();
        final ButterKnife butterKnife = ButterKnifeFactory.getForModule(getProject(), javaFile);
        final PsiClass butterknifeinCLassPath = JavaPsiFacade.getInstance(getProject()).findClass("butterknife.ButterKnife",
                GlobalSearchScope.everythingScope(getProject()));

        if (butterKnife == null || butterknifeinCLassPath == null) {
            Notification.showError(getProject(),
                    "ButterKnife is not available in current project\n" +
                    "Missing dependencies in classpath?");
            return;
        }

        addMissingImports(butterknifeinCLassPath);

        if (mEntity.isHolderSelection()) {
            generateAdapter(butterKnife);
        } else {
            if (WriteAction.getInjectCount(mEntity.getFoundXmlElementIds()) > 0) {
                generateFields(butterKnife);
            }
            generateInjects(butterKnife);
            if (WriteAction.getClickCount(mEntity.getFoundXmlElementIds()) > 0) {
                generateClick();
            }
            Notification.showInfo(getProject(),
                    WriteAction.getInjectCount(mEntity.getFoundXmlElementIds())
                            + " injections and " +
                            WriteAction.getClickCount(mEntity.getFoundXmlElementIds())
                            + " onClick added to "
                            + javaFile.getName()
            );
        }

        // reformat code
        reformatJavaCode();
    }

    private void reformatJavaCode(){
        final PsiJavaFile currFile = mEntity.getJavaFile();
        JavaCodeStyleManager styleManager = JavaCodeStyleManager.getInstance(getProject());
        styleManager.optimizeImports(currFile);
        styleManager.shortenClassReferences(currFile);

        new ReformatCodeProcessor(getProject(), currFile, null, false)
                .runWithoutProgress();
    }

    public static String getViewHolderClassName() {
        return PropertiesComponent
                .getInstance()
                .getValue(Settings.VIEWHOLDER_CLASS_NAME, "ViewHolder");
    }
}