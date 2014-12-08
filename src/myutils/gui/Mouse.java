package myutils.gui;

import myutils.Vector2D;

@SuppressWarnings({"WeakerAccess", "CanBeFinal", "UnusedDeclaration"})
public class Mouse {
    public Vector2D locationOnScreenScaled = new Vector2D();
    public Vector2D locationRelativeScaled = new Vector2D();
    public Vector2D locationOnScreenAbsolute = new Vector2D();
    public Vector2D locationRelativeAbsolute = new Vector2D();
    public boolean clicked = false;
    @SuppressWarnings("FieldCanBeLocal")
    private int x;
    @SuppressWarnings("FieldCanBeLocal")
    private int y;
    private int numTimesClicked = 0;

    public void toggle(int xPos, int yPos, boolean isClicked, Pixels screen) {
        x = xPos;
        y = yPos;
        screen.convertOnScreenScaled(locationOnScreenScaled, x, y);
        screen.convertRelativeScaled(locationRelativeScaled, x, y);
        screen.convertOnScreenAbsolute(locationOnScreenAbsolute, x, y);
        screen.convertRelativeAbsolute(locationRelativeAbsolute, x, y);
        if (clicked = isClicked)
            numTimesClicked++;
    }

    public void reset() {
        this.numTimesClicked = 0;
    }
}
