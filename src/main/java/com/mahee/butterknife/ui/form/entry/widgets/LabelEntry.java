package com.mahee.butterknife.ui.form.entry.widgets;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Magesh.K on 2/12/2021.
 */
public class LabelEntry {

    private final JLabel mLabel = new JLabel();

    public LabelEntry(final String title){
        mLabel.setText(title);
        mLabel.setPreferredSize(new Dimension(100, 26));
        mLabel.setFont(new Font(mLabel.getFont().getFontName(), Font.BOLD, mLabel.getFont().getSize()));
    }
    public JLabel getLabel(){
        return mLabel;
    }
}
