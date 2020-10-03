package mazes;

import java.awt.Graphics;
import javax.swing.JFrame;
import javax.swing.JPanel;

class MazePanel extends JPanel {

    int[] lines;
    float cellWidth;
    Graphics graphics;

    MazePanel (int[] l, int w, int h) {
        this.lines = l;
        float a = w;
        if (h > a) {
            a = h;
        }
        this.cellWidth = 600/a;
    }

    private int coordinate(int c) {
        return Math.round(20 + this.cellWidth*c);
    }

    public void paint (Graphics g) {
        this.graphics = g;
        for (int i = 0; i < this.lines.length-4; i+=4) {
            int x1 = coordinate(this.lines[i]);
            int y1 = coordinate(this.lines[i+1]);
            int x2 = coordinate(this.lines[i+2]);
            int y2 = coordinate(this.lines[i+3]);
            g.drawLine(x1, y1, x2, y2);
        }
    }
}