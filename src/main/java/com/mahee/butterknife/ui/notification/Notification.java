package com.mahee.butterknife.ui.notification;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.ui.awt.RelativePoint;

/**
 * Created by Magesh.K on 2/11/2021.
 */
public class Notification {
    private static final int fadeoutMs = 9000;

    public static void showWarning(Project project, String text){
        Notify(project,MessageType.WARNING,text);
    }
    public static void showInfo(Project project, String text){
        Notify(project,MessageType.INFO,text);
    }

    public static void showError(Project project, String text){
        Notify(project,MessageType.ERROR,text);
    }

    private static void Notify(Project project, MessageType type, String text) {
        Notify(project,type,text,Balloon.Position.atRight);
    }

    public static void Notify(final Project project,
                                        final MessageType type,
                                        final String text,
                                        final Balloon.Position position) {
        StatusBar statusBar = WindowManager.getInstance().getStatusBar(project);

        JBPopupFactory.getInstance()
                .createHtmlTextBalloonBuilder(text, type, null)
                .setFadeoutTime(fadeoutMs)
                .createBalloon()
                .show(RelativePoint.getSouthEastOf(statusBar.getComponent()), position);
    }
}
