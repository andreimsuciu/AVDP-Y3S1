package ro.ubb;

public class Position {
    private int heightTopLeft;
    private int widthTopLeft;

    public Position(int heightTopLeft, int widthTopLeft) {
        this.heightTopLeft = heightTopLeft;
        this.widthTopLeft = widthTopLeft;
    }

    public int getHeightTopLeft() {
        return heightTopLeft;
    }

    public int getWidthTopLeft() {
        return widthTopLeft;
    }
}
