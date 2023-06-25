package com.dama.utils;

import androidx.annotation.NonNull;

public class Cell{
    private int row;
    private int col;

    public Cell(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public boolean isValidPosition(){
        return row >= 0 && col >= 0;
    }

    @NonNull
    @Override
    public String toString() {
        return "Cell{" +
                "row=" + row +
                ", col=" + col +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cell cell = (Cell) o;
        return row == cell.row && col == cell.col;
    }

    public double distanceFromCell(Cell cell){
        double distanceQuad = Math.pow((cell.getRow()-this.row),2)+Math.pow((cell.getCol()-this.getCol()),2);
        double distance = Math.sqrt((distanceQuad));
        return distance;
    }
}
