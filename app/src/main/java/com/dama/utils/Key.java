package com.dama.utils;

import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;

public class Key implements Cloneable{
    private int code;
    private String label;
    private Drawable icon;
    private boolean suggestion;

    public Key(int code, String label, Drawable icon) {
        this.code = code;
        this.label = label;
        this.icon = icon;
        this.suggestion = false;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public boolean isSuggestion() {
        return suggestion;
    }

    public void setSuggestion(boolean suggestion) {
        this.suggestion = suggestion;
    }

    @NonNull
    @Override
    public Key clone(){
        try {
            Key key = (Key) super.clone();
            return key;
        }catch (CloneNotSupportedException e){
            return null;
        }
    }

    @Override
    public String toString() {
        return "Key{" +
                "code=" + code +
                ", label='" + label + '\'' +
                ", suggestion=" + suggestion +
                '}';
    }
}
