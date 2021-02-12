package com.mahee.butterknife.ui.form.entry.widgets;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemListener;

/**
 * Created by Magesh.K on 2/11/2021.
 */
public class CheckboxEntry {
    public interface CheckBoxEntryChangeListener extends ItemListener { }

    private final JCheckBox mCheckEntry = new JCheckBox();

    public CheckboxEntry(CheckBoxEntryChangeListener listener){
        this();
        mCheckEntry.addItemListener(listener);
    }

    public CheckboxEntry(){
        mCheckEntry.setPreferredSize(new Dimension(40, 26));
        mCheckEntry.setSelected(false);
    }

    public JCheckBox getCheckBox(){
        return mCheckEntry;
    }
}
