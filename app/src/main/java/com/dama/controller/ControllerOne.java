package com.dama.controller;

import android.inputmethodservice.Keyboard;
import com.dama.views.CursorSpaceView;
import com.dama.views.KeyboardView;

public class ControllerOne extends Controller{
    public ControllerOne(Keyboard keyboard, CursorSpaceView cursorSpaceView, KeyboardView keyboardView) {
        super(keyboard, cursorSpaceView, keyboardView);
    }
}
