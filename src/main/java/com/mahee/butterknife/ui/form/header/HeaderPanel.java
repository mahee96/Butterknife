package com.mahee.butterknife.ui.form.header;

import com.mahee.butterknife.common.Constants;
import com.mahee.butterknife.ui.form.list.CheckBoxSelectionListener;
import com.mahee.butterknife.ui.form.header.widgets.CheckboxSelectAll;
import com.mahee.butterknife.ui.form.header.widgets.LabelHeader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;

public class HeaderPanel extends JPanel
        implements Constants, CheckboxSelectAll.CheckboxSelectAllChangeListener {

    private final static String TAG = HeaderPanel.class.getSimpleName();

    private final CheckboxSelectAll mCheckboxSelectAll = new CheckboxSelectAll(this);
    private CheckBoxSelectionListener mListener;

    public void setListener(CheckBoxSelectionListener listener){
        mListener = listener;
    }

    public CheckBoxSelectionListener getListener(){
        return mListener;
    }

    public HeaderPanel() {
        createHeaderPanel();
    }

    private void createHeaderPanel() {
        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
        add(Box.createRigidArea(new Dimension(1, 0)));
        add(mCheckboxSelectAll.getCheckBox());
        add(Box.createRigidArea(new Dimension(11, 0)));
        add(new LabelHeader(Dialog.Header.ELEMENT).getLabel());
        add(Box.createRigidArea(new Dimension(12, 0)));
        add(new LabelHeader(Dialog.Header.ID).getLabel());
        add(Box.createRigidArea(new Dimension(12, 0)));
        add(new LabelHeader(Dialog.Header.ON_CLICK).getLabel());
        add(Box.createRigidArea(new Dimension(22, 0)));
        add(new LabelHeader(Dialog.Header.VARIABLE_NAME).getLabel());
        add(Box.createHorizontalGlue());
    }

    public void setSelectAllState(boolean state) {
        mCheckboxSelectAll.getCheckBox().setSelected(state);
    }

    public boolean getSelectAllState() {
        return mCheckboxSelectAll.getCheckBox().isSelected();
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        if (mListener != null)
            mListener.onStateChanged(getSelectAllState());
    }
}
