package com.dama.views;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
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
        //moveCursor(keyView);
        initPosition(keyView);
    }

    private void initPosition(KeyView keyView){
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

    public void moveCursor(KeyView keyView){
        //calculate destination coordinates
        Rect offsetViewBounds = new Rect();
        keyView.getDrawingRect(offsetViewBounds);
        offsetDescendantRectToMyCoords(keyView, offsetViewBounds);
        int x = offsetViewBounds.left;
        int y = offsetViewBounds.top;

        //create animation
        TranslateAnimation animation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0f, Animation.ABSOLUTE, x - cursor.getX(),
                Animation.RELATIVE_TO_SELF, 0f, Animation.ABSOLUTE, y - cursor.getY()
        );
        animation.setDuration(350);

        //add listener
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //final view's position at the animation's end
                cursor.clearAnimation();
                LayoutParams layoutParams = new LayoutParams(
                        LayoutParams.WRAP_CONTENT,
                        LayoutParams.WRAP_CONTENT
                );
                layoutParams.leftMargin = x;
                layoutParams.topMargin = y;
                cursor.setLayoutParams(layoutParams);

                cursor.changeDimension(keyView.getKeyHeight(), keyView.getKeyWidth(), 0);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        //start animation on view
        cursor.startAnimation(animation);
    }

    public void destroyCursor(){
        removeView(cursor);
    }
}
