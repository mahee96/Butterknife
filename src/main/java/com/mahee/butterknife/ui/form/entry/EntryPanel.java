package com.mahee.butterknife.ui.form.entry;

import com.mahee.butterknife.common.Constants;
import com.mahee.butterknife.data.Entity;
import com.mahee.butterknife.model.XmlElement;
import com.mahee.butterknife.ui.form.entry.widgets.CheckboxEntry;
import com.mahee.butterknife.ui.form.entry.widgets.LabelEntry;
import com.mahee.butterknife.ui.form.entry.widgets.TextFieldEntry;
import com.mahee.butterknife.ui.form.list.CheckBoxSelectionListener;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.util.ArrayList;
import java.util.List;

public class EntryPanel extends JPanel implements Constants,
        CheckboxEntry.CheckBoxEntryChangeListener,
        TextFieldEntry.TextBoxFocusListener {

    private final Entity mEntity;
    private final XmlElement mViewElement;

    private final CheckboxEntry mCheck = new CheckboxEntry();
    private final LabelEntry mType;
    private final LabelEntry mID;
    private final CheckboxEntry mEvent;
    private final TextFieldEntry mName;

    private CheckBoxSelectionListener mListener;

    public boolean isSelected() {
        return mCheck.getCheckBox().isSelected();
    }

    public void setSelected(final boolean state) {
        mCheck.getCheckBox().setSelected(state);
    }

    public void setListener(@Nullable final CheckBoxSelectionListener listener){
        mListener = listener;
    }

    public CheckBoxSelectionListener getListener(){
        return mListener;
    }

    public EntryPanel(final Entity entity, final XmlElement viewElement) {
        
        mViewElement = viewElement;
        mEntity = entity;
        mType = new LabelEntry(mViewElement.name);
        mID = new LabelEntry(mViewElement.id);
        mEvent = new CheckboxEntry();
        mName = new TextFieldEntry(this, mViewElement.fieldName);

        if (!mEntity.getExistingStringIds().contains(mViewElement.getFullID()))
            mCheck.getCheckBox().setSelected(mViewElement.used);

        createEntryPanel();
    }

    private void createEntryPanel() {
        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
        setMaximumSize(new Dimension(Short.MAX_VALUE, 54));
        add(mCheck.getCheckBox());
        add(Box.createRigidArea(new Dimension(10, 0)));
        add(mType.getLabel());
        add(Box.createRigidArea(new Dimension(10, 0)));
        add(mID.getLabel());
        add(Box.createRigidArea(new Dimension(10, 0)));
        add(mEvent.getCheckBox());
        add(Box.createRigidArea(new Dimension(10, 0)));
        add(mName.getTextField());
        add(Box.createHorizontalGlue());
    }

    public void syncXmlElement() {
        mViewElement.used = mCheck.getCheckBox().isSelected();
        mViewElement.isClick = mEvent.getCheckBox().isSelected();
        mViewElement.fieldName = mName.getTextField().getText();
        if (mViewElement.checkValidity()) {
            mName.setNormalBackground();
        } else {
            mName.setErrorBackground();
        }
    }

    private void updateStates(final boolean selected) {
        if (selected) {
            mType.getLabel().setEnabled(true);
            mID.getLabel().setEnabled(true);
            mName.getTextField().setEnabled(true);
        } else {
            mType.getLabel().setEnabled(false);
            mID.getLabel().setEnabled(false);
            mName.getTextField().setEnabled(false);
        }
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        final boolean selected = mCheck.getCheckBox().isSelected();
        updateStates(selected);
        if (mListener != null) mListener.onStateChanged(selected);
    }

    @Override
    public void focusLost(FocusEvent e) {
        syncXmlElement();
    }
}
