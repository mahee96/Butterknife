package com.mahee.butterknife.ui.form.header.widgets;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * Created by Magesh.K on 2/11/2021.
 */
public class CheckboxSelectAll {
    public interface CheckboxSelectAllChangeListener extends ItemListener { }

    private final JCheckBox mCheckAll = new JCheckBox();

    public CheckboxSelectAll(CheckboxSelectAllChangeListener listener){
        mCheckAll.setPreferredSize(new Dimension(40, 26));
        mCheckAll.setSelected(false);
        mCheckAll.addItemListener(listener);
    }

    public JCheckBox getCheckBox(){
        return mCheckAll;
    }
}
