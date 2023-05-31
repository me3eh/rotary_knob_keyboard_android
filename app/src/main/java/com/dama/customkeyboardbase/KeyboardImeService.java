package com.dama.customkeyboardbase;

import android.annotation.SuppressLint;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.media.AudioManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.FrameLayout;
import com.dama.controller.Controller;
import com.dama.controller.ControllerOne;
import com.dama.utils.Cell;
import com.dama.utils.Key;
import com.dama.views.CursorSpaceView;
import com.dama.views.KeyboardView;

public class KeyboardImeService extends InputMethodService {
    private Controller controller;
    private boolean keyboardShown = false;
    private Keyboard qwertyKeyboard;
    private CursorSpaceView cursorSpaceView;
    private KeyboardView keyboardView;
    private FrameLayout rootView;

    @SuppressLint("InflateParams")
    @Override
    public View onCreateInputView() {
        rootView = (FrameLayout) this.getLayoutInflater().inflate(R.layout.keyboard_layout, null);

        qwertyKeyboard = new Keyboard(this, R.xml.querty);

        cursorSpaceView = rootView.findViewById(R.id.cursorSpace_view);
        keyboardView = cursorSpaceView.findViewById(R.id.keyboard_view);

        ControllerOne cOne = new ControllerOne(qwertyKeyboard,cursorSpaceView,keyboardView);
        controller = cOne;
        return rootView;
    }

    @Override
    public void onStartInputView(EditorInfo info, boolean restarting) {
        super.onStartInputView(info, restarting);
        keyboardShown = true;
        //Log.d("KeyboardImeService", "onStartInputView");
    }

    @Override
    public void onFinishInputView(boolean finishingInput) {
        super.onFinishInputView(finishingInput);
        keyboardShown = false;
        //Log.d("KeyboardImeService", "onFinishInputView");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyboardShown){
            //Log.d("KeyboardImeService", "onKeyDown --> "+ keyCode);
            InputConnection ic = getCurrentInputConnection();
            playClick(keyCode);
            switch (keyCode){
                case KeyEvent.KEYCODE_BACK:
                    hideKeyboard();
                    break;
                case KeyEvent.KEYCODE_DPAD_LEFT:
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                case KeyEvent.KEYCODE_DPAD_UP:
                case KeyEvent.KEYCODE_DPAD_DOWN:
                    Cell newCell = controller.newFocus(keyCode);
                    if (controller.isNextFocusable(newCell, keyCode)){
                        controller.setFocus(newCell);
                    }
                    break;
                case KeyEvent.KEYCODE_DPAD_CENTER:
                    Cell focus = controller.getFocus();
                    Key key = controller.getKeysAtRow(focus.getRow()).get(focus.getCol());
                    //write char
                    handleText(key.getCode(), ic);
                    char c = (char) key.getCode();
                    break;
            }
            return true;
        }
        return false;
    }

    private void handleText(int code, InputConnection ic){
        switch (code){
            case 24: //user press backspace
                ic.deleteSurroundingText(1, 0);
                break;
            default: //user press letter, number, symbol
                char c = (char) code;
                ic.commitText(String.valueOf(c),1);
        }
    }

    private void hideKeyboard(){
        requestHideSelf(0); //calls onFinishInputView
    }

    private void playClick(int keyCode){
        AudioManager am = (AudioManager) getSystemService(AUDIO_SERVICE);
        switch (keyCode){
            case Controller.SPACE_KEY:
                am.playSoundEffect(AudioManager.FX_KEYPRESS_SPACEBAR);
                break;
            case Keyboard.KEYCODE_DONE:
            case 10:
                am.playSoundEffect(AudioManager.FX_KEYPRESS_RETURN);
                break;
            case Keyboard.KEYCODE_DELETE:
                am.playSoundEffect(AudioManager.FX_KEYPRESS_DELETE);
                break;
            default:
                am.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD);
                break;
        }
    }
}
