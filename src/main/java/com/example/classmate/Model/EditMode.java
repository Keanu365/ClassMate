package com.example.classmate.Model;

public enum EditMode {
    SELECT,
    MOVE,
    PAN,
    EDIT_TEXT,
    RESIZE;

    public static EditMode[] getEditModes(){
        return new EditMode[]{SELECT,MOVE,PAN,EDIT_TEXT,RESIZE};
    }
}
