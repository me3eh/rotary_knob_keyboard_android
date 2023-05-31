package com.dama.views;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.dama.customkeyboardbase.R;

public class CursorSpaceView extends FrameLayout {
    private KeyView cursor;

    public CursorSpaceView(@NonNull Context context) {
        super(context);
    }

    public CursorSpaceView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CursorSpaceView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void initCursorSpaceView(KeyView keyView){
        //init cursor
        Drawable cs = getResources().getDrawable(R.drawable.cursor);
        cursor = new KeyView(getContext(), cs, null, "#FBFBFB");
        moveCursor(keyView);
    }

    public void moveCursor(KeyView keyView){
        keyView.post(() -> {
            Rect offsetViewBounds = new Rect();
            keyView.getDrawingRect(offsetViewBounds);

            offsetDescendantRectToMyCoords(keyView, offsetViewBounds);
            int x = offsetViewBounds.left;
            int y = offsetViewBounds.top;

            removeView(cursor);
            LayoutParams layoutParams = new LayoutParams(
                    LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT
            );
            layoutParams.leftMargin = x; //set x
            layoutParams.topMargin = y; //set y
            cursor.setLayoutParams(layoutParams);
            addView(cursor);

            cursor.changeDimension(keyView.getKeyHeight(), keyView.getKeyWidth(), 0);
        });
    }

    public void destroyCursor(){
        removeView(cursor);
    }
}
