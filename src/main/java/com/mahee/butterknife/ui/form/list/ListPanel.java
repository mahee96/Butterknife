package com.mahee.butterknife.ui.form.list;

import com.intellij.ui.components.JBScrollPane;
import com.mahee.butterknife.common.Constants;
import com.mahee.butterknife.data.Entity;
import com.mahee.butterknife.model.XmlElement;
import com.mahee.butterknife.ui.form.CancelListener;
import com.mahee.butterknife.ui.form.ConfirmListener;
import com.mahee.butterknife.ui.form.entry.EntryPanel;
import com.mahee.butterknife.ui.form.header.HeaderPanel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Magesh.K on 2/11/2021.
 */
public class ListPanel extends JPanel implements Constants {


    private final Entity mEntity;
    private final List<EntryPanel> mEntries = new ArrayList<>();

    private boolean mHolderSelection;

    private final ConfirmListener mConfirmListener;
    private final CancelListener mCancelListener;

    private final JCheckBox mHolderCheckBox = new JCheckBox();
    private final JCheckBox mSplitOnclickMethodsCheck = new JCheckBox();

    private final JButton mConfirm = new JButton();
    private final JButton mCancel = new JButton();

    private final CheckBoxSelectionListener mSelectAllListener = (selected) -> {
        for (final EntryPanel entryPanel : mEntries) {
            final CheckBoxSelectionListener listener = entryPanel.getListener();
            entryPanel.setListener(null);
            entryPanel.setSelected(selected);
            entryPanel.setListener(listener);
        }
    };
    private final HeaderPanel mHeaderPanel = new HeaderPanel();

    private final CheckBoxSelectionListener mSingleCheckListener = (selected) -> {
        boolean state = true;
        for (EntryPanel entryPanel : mEntries) {
            state &= entryPanel.isSelected();
        }
        mHeaderPanel.setSelectAllState(state);
    };

    public ListPanel(@NotNull Entity entity,
                     @Nullable final ConfirmListener confirmListener,
                     @Nullable final CancelListener cancelListener) {

        mEntity = entity;
        mConfirmListener = confirmListener;
        mCancelListener = cancelListener;

        mHeaderPanel.setListener(mSelectAllListener);
        setPreferredSize(new Dimension(640, 360));
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        add(this.createListPanel(), BorderLayout.CENTER);
        refresh();
        add(this.createHolderPanel(), BorderLayout.PAGE_END);
        add(this.createSplitOnClickMethodPanel(), BorderLayout.PAGE_END);
        add(this.createButtonsPanel(), BorderLayout.PAGE_END);
        refresh();
    }

    private JPanel createListPanel() {
        final JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.PAGE_AXIS));
        listPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        listPanel.add(mHeaderPanel);
        listPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        final JBScrollPane scrollPane = new JBScrollPane(this.createEntriesPanel());
        listPanel.add(scrollPane);
        return listPanel;
    }

    private JPanel createEntriesPanel() {
        final JPanel entriesPanel = new JPanel();
        entriesPanel.setLayout(new BoxLayout(entriesPanel, BoxLayout.PAGE_AXIS));

        boolean selectAllCheck = true;
        for (XmlElement viewElement : mEntity.getFoundXmlElementIds()) {
            final EntryPanel entryPanel = new EntryPanel(mEntity, viewElement);
            entryPanel.setListener(mSingleCheckListener);
            selectAllCheck &= entryPanel.isSelected();

            entriesPanel.add(Box.createRigidArea(new Dimension(0, 5)));
            entriesPanel.add(entryPanel);
            mEntries.add(entryPanel);
        }
        mHeaderPanel.setSelectAllState(selectAllCheck);

        entriesPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        entriesPanel.add(Box.createVerticalGlue());
        entriesPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        return entriesPanel;
    }

    private JPanel createSplitOnClickMethodPanel() {
        mSplitOnclickMethodsCheck.setPreferredSize(new Dimension(32, 26));
        mSplitOnclickMethodsCheck.setSelected(false);

        final JLabel splitOnClickMethodsLabel = new JLabel();
        splitOnClickMethodsLabel.setText(Dialog.SPLIT_ON_CLICK_METHODS_LABEL);

        final JPanel splitOnclickMethodsPanel = new JPanel();
        splitOnclickMethodsPanel.setLayout(new BoxLayout(splitOnclickMethodsPanel, BoxLayout.LINE_AXIS));
        splitOnclickMethodsPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        splitOnclickMethodsPanel.add(mSplitOnclickMethodsCheck);
        splitOnclickMethodsPanel.add(splitOnClickMethodsLabel);
        splitOnclickMethodsPanel.add(Box.createHorizontalGlue());
        return splitOnclickMethodsPanel;
    }

    private JPanel createHolderPanel() {
        mHolderCheckBox.setPreferredSize(new Dimension(32, 26));
        mHolderCheckBox.setSelected(mHolderSelection);
        mHolderCheckBox.addChangeListener((event) -> mHolderSelection = mHolderCheckBox.isSelected());

        final JLabel holderLabel = new JLabel();
        holderLabel.setText(Dialog.HOLDER_LABEL);

        final JPanel holderPanel = new JPanel();
        holderPanel.setLayout(new BoxLayout(holderPanel, BoxLayout.LINE_AXIS));
        holderPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        holderPanel.add(mHolderCheckBox);
        holderPanel.add(holderLabel);
        holderPanel.add(Box.createHorizontalGlue());
        return holderPanel;
    }

    private JPanel createButtonsPanel() {
        final JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        buttonPanel.add(Box.createHorizontalGlue());
        buttonPanel.add(this.createConfirmButton());
        buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        buttonPanel.add(this.createCancelButton());
        return buttonPanel;
    }

    private JButton createConfirmButton() {
        mConfirm.setPreferredSize(new Dimension(120, 26));
        mConfirm.setAction(new AbstractAction(Dialog.CONFIRM_BUTTON_TEXT) {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean valid = checkValidity();
                for (EntryPanel entryPanel : mEntries) {
                    entryPanel.syncXmlElement();
                }
                if (valid && mConfirmListener != null) {
                    mConfirmListener.onConfirm(mEntity);
                }
            }
        });
        mConfirm.setVisible(true);
        return mConfirm;
    }

    private JButton createCancelButton() {
        mCancel.setPreferredSize(new Dimension(120, 26));
        mCancel.setAction(new AbstractAction(Dialog.CANCEL_BUTTON_TEXT) {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (mCancelListener != null) {
                    mCancelListener.onCancel();
                }
            }
        });
        mCancel.setVisible(true);
        return mCancel;
    }

    private void refresh() {
        revalidate();

        if (mConfirm != null) {
            mConfirm.setVisible(mEntity.getFoundXmlElementIds().size() > 0);
        }
    }

    private boolean checkValidity() {
        boolean valid = true;
        for (XmlElement xmlElement : mEntity.getFoundXmlElementIds()) {
            if (!xmlElement.checkValidity()) {
                valid = false;
            }
        }
        return valid;
    }

    public JButton getConfirmButton() {
        return mConfirm;
    }

}
