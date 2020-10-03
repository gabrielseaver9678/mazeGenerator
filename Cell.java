package mazes;

class Cell {
    public boolean top = true;
    public boolean left = true;
    public boolean right = true;
    public boolean bottom = true;
    public boolean done = false;
    public int x;
    public int y;

    Cell (int a, int b) {
        this.x = a;
        this.y = b;
    }
}