package com.mahee.butterknife.common;

public interface Constants {
    String DELIM_SPACE = " ";
    interface App{
        String NAME = "ButterKnife for Android";
        String VERSION = "v1.0.0";
        String FULL_NAME = NAME + DELIM_SPACE + VERSION;
    }
    interface Writer{
        String WRITE_ACTION_COMMAND = "ButterKnife View Injections";
    }
    interface Messages{
        interface INFO{
            String NONE_SELECTED = "No injection was selected";
        }
        interface ERROR{
            String NO_IDs_FOUND = "No IDs found in layout.xml";
            String NO_LAYOUT_FOUND = "No layout found at the current line\n" +
                    "Or Layout file not found in the resources dir";
        }
    }
    interface LogTag{
        interface INFO{
            String LAYOUT_FILE = "Layout file: ";
        }
    }
    interface XmlParserTag {
        String INCLUDE = "include";

        interface Attribute {
            String ID = "android:id";
            String CLASS = "class";
            String LAYOUT = "layout";
        }
    }
    interface JavaParserTag{
        String LAYOUT = "R.layout";
    }
    interface Dialog{
        String HOLDER_LABEL = "Create ViewHolder";
        String SPLIT_ON_CLICK_METHODS_LABEL = "Split OnClick methods";
        String CONFIRM_BUTTON_TEXT = "Confirm";
        String CANCEL_BUTTON_TEXT = "Cancel";

        interface Header{
            String ELEMENT = "Element";
            String ID = "Id";
            String ON_CLICK = "OnClick";
            String VARIABLE_NAME = "Variable Name";
        }
        interface List{

        }
        interface Entry{

        }
    }
}
