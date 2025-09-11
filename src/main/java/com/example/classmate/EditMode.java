package com.example.classmate;

enum EditMode {
    SELECT,
    MOVE,
    PAN,
    EDIT_TEXT,
    RESIZE;

    static EditMode[] getEditModes(){
        return new EditMode[]{SELECT,MOVE,PAN,EDIT_TEXT,RESIZE};
    }
}
