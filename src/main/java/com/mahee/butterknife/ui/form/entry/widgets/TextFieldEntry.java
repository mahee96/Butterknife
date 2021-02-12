package com.mahee.butterknife.ui.form.entry.widgets;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

/**
 * Created by Magesh.K on 2/12/2021.
 */
public class TextFieldEntry extends JTextField {
    public interface TextBoxFocusListener extends FocusListener{
        //Not interested in focusGained(0
        default void focusGained(FocusEvent e) {}
    }

    private final JTextField mTextEntry = new JTextField(10);
    private final Color mNameDefaultColor = mTextEntry.getBackground();
    private final Color mNameErrorColor = new Color(0xc60000);

    public TextFieldEntry() {
        this("");
    }

    public TextFieldEntry(final TextBoxFocusListener listener) {
        this("");
        mTextEntry.addFocusListener(listener);
    }
    public TextFieldEntry(final TextBoxFocusListener listener, final String text) {
        this(text);
        mTextEntry.addFocusListener(listener);
    }

    public TextFieldEntry(final String text){
        mTextEntry.setText(text);
        mTextEntry.setPreferredSize(new Dimension(100, 26));
    }

    public JTextField getTextField(){
        return mTextEntry;
    }

    public void setNormalBackground(){
        mTextEntry.setBackground(mNameDefaultColor);
    }

    public void setErrorBackground(){
        mTextEntry.setBackground(mNameErrorColor);
    }
}
