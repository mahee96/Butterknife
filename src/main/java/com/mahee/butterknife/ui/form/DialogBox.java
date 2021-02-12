package com.mahee.butterknife.ui.form;

import com.mahee.butterknife.action.WriteAction;
import com.mahee.butterknife.common.Constants;
import com.mahee.butterknife.data.Entity;
import com.mahee.butterknife.model.XmlElement;
import com.mahee.butterknife.ui.form.list.ListPanel;
import com.mahee.butterknife.ui.notification.Notification;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.List;

/**
 * Created by Magesh.K on 2/11/2021.
 */
public class DialogBox implements Constants {
    private static final String TAG = DialogBox.class.getSimpleName();

    private static JFrame mDialogBox;

    private static final CancelListener mCancelListener = DialogBox::closeDialog;

    private static final ConfirmListener mConfirmListener = new ConfirmListener() {
        @Override
        public void onConfirm(final Entity entity) {
            closeDialog();
            if (WriteAction.getInjectCount(entity.getFoundXmlElementIds()) > 0
                    || WriteAction.getClickCount(entity.getFoundXmlElementIds()) > 0) { // generate injections
                WriteAction.Builder(entity, Writer.WRITE_ACTION_COMMAND).execute();
            } else {
                Notification.showWarning(entity.getProject(), Messages.INFO.NONE_SELECTED);
            }
        }
    };

    public static void showDialog(@NotNull final Entity entity) {

        final ListPanel panel = new ListPanel(entity, mConfirmListener, mCancelListener);

        if (mDialogBox != null) mDialogBox.dispose();    // prevent memory leaks
        mDialogBox = new JFrame();
        mDialogBox.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        mDialogBox.getRootPane().setDefaultButton(panel.getConfirmButton());
        mDialogBox.getContentPane().add(panel);
        mDialogBox.pack();
        mDialogBox.setLocationRelativeTo(null);
        mDialogBox.setTitle(App.FULL_NAME);
        mDialogBox.setVisible(true);
    }

    public static void closeDialog() {
        if (mDialogBox == null) return;
        mDialogBox.setVisible(false);
        mDialogBox.dispose();
    }
}
