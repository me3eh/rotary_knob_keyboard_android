package com.dama.controllers;

import android.widget.FrameLayout;
import com.dama.customkeyboardbase.R;
import com.dama.utils.Cell;
import com.dama.utils.Key;
import com.dama.views.ContainerView;
import com.dama.views.KeyView;
import com.dama.views.KeyboardView;
import java.util.ArrayList;
import java.util.HashMap;

public class ViewsController {
    private ContainerView containerView;
    private KeyboardView keyboardView;

    public ViewsController(FrameLayout rootView) {
        containerView = rootView.findViewById(R.id.container_view);
        keyboardView = containerView.findViewById(R.id.keyboard_view);
    }

    public void drawKeyboard(HashMap<Integer, ArrayList<Key>> keys, Cell focus){
        keyboardView.initKeyboardView(keys);

        KeyView keyView = keyboardView.getKeyViewAtCell(focus);
        containerView.initContainerView(keyView);
    }

    public void moveCursorPosition(Cell focus){
        KeyView keyView = keyboardView.getKeyViewAtCell(focus);
        containerView.moveCursor(keyView);
    }

    public void setUpdatedCursorCoordinates(Cell focus){
        KeyView keyView = keyboardView.getKeyViewAtCell(focus);
        containerView.prova(keyView);
    }

    public void modifyKeyLabel(Cell position, String label){
        String finaLabel;
        if(label.equals(" "))
            finaLabel = "␣";
        else
            finaLabel = label;

        keyboardView.getKeyViewAtCell(position).changeLabel(finaLabel, "#FBFBFB");
    }

    public void removeKeyboard(){
        keyboardView.deleteAll();
    }

}
