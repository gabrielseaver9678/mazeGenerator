package mazes;

import java.util.ArrayList;

class Matrix {
    ArrayList<Cell[]> matrix = new ArrayList<Cell[]>();

    int width;
    int height;

    //Gets cell in matrix
    public Cell getCellAt(int x, int y) {
        return this.matrix.get(y)[x];
    }

    //Because of how each cell has its own walls, there should be a method to break or put up both at a time
    public void setCellBarrier(int x, int y, String wall, boolean setTo) {
        if (wall == "top") {
            getCellAt(x, y).top = setTo;
            if (y-1 >= 0) {
                getCellAt(x, y-1).bottom = setTo;
            }
        } else if (wall == "left") {
            getCellAt(x, y).left = setTo;
            if (x-1 >= 0) {
                getCellAt(x-1, y).right = setTo;
            }
        } else if (wall == "right") {
            getCellAt(x, y).right = setTo;
            if (x+1 < this.width) {
                getCellAt(x+1, y).left = setTo;
            }
        } else if (wall == "bottom") {
            getCellAt(x, y).bottom = setTo;
            if (y+1 < this.height) {
                getCellAt(x, y+1).top = setTo;
            }
        } else {
            System.out.println("Error: wall parameter to setCellBarrier method must be top, bottom, left, or right.");
        }
    }

    private void removeRedundantWalls() {
        /*Because each cell, even adjacent ones, all have separately defined walls but should in theory still be the same 
        ((0, 0).right == (1, 0).left should be true), we can get rid of the redundant cell walls
        to make drawing the maze take fewer lines*/
        for (int y = 0; y < this.height; y++) {
            for (int x = 0; x < this.width; x++) {
                if (x > 0) {
                    getCellAt(x, y).left = false;
                }
                if (y > 0) {
                    getCellAt(x, y).top = false;
                }
            }
        }
    }

    public int[] getRandomVelocity(int x, int y, int hBias) {
        int[] possibleChanges = new int[400]; //This will be an array of possible movement vectors
        int i = 0; //This is the current index in possibleChanges
        int leftBias = hBias;
        int rightBias = hBias;
        int topBias = 100-hBias;
        int bottomBias = 100-hBias;
        if (x-1 >= 0) {
            if (!getCellAt(x-1, y).done) {
                for (int b = 0; b < leftBias; b++) {
                    possibleChanges[i] = -1; //x
                    possibleChanges[i+1] = 0; //y
                    i += 2;
                }
            }
        }
        if (y-1 >= 0) {
            if (!getCellAt(x, y-1).done) {
                for (int b = 0; b < topBias; b++) {
                    possibleChanges[i] = 0; //x
                    possibleChanges[i+1] = -1; //y
                    i += 2;
                }
            }
        }
        if (x+1 < this.width) {
            if (!getCellAt(x+1, y).done) {
                for (int b = 0; b < rightBias; b++) {
                    possibleChanges[i] = 1; //x
                    possibleChanges[i+1] = 0; //y
                    i += 2;
                }
            }
        }
        if (y+1 < this.height) {
            if (!getCellAt(x, y+1).done) {
                for (int b = 0; b < bottomBias; b++) {
                    possibleChanges[i] = 0; //x
                    possibleChanges[i+1] = 1; //y
                    i += 2;
                }
            }
        }
        if (i == 0) {
            int[] s = {0};
            return s; //No adjacent unfinished cells exist
        } else {
            int rand = ((int) Math.floor((Math.random()*(i/2))))*2; //Random index selected from possibleChanges array (only even indicies included)
            int[] s = new int[2];
            s[0] = possibleChanges[rand];
            s[1] = possibleChanges[rand+1];
            return s;
        }
    }

    public Cell[] getConnectedCellsOnRow(int y) {
        Cell[] clist = new Cell[this.width];

        int i = 0;
        for (int x = 0; x < this.width; x++) {
            Cell c = getCellAt(x, y);
            if (x > 0) {
                if (getCellAt(x-1, y).done && !getCellAt(x, y).done) {
                    clist[i] = c;
                    i++;
                    continue;
                }
            }
            if (x < this.width-1) {
                if (getCellAt(x+1, y).done && !getCellAt(x, y).done) {
                    clist[i] = c;
                    i++;
                    continue;
                }
            }
            if (y > 0) {
                if (getCellAt(x, y-1).done && !getCellAt(x, y).done) {
                    clist[i] = c;
                    i++;
                    continue;
                }
            }
            if (y < this.height-1) {
                if (getCellAt(x, y+1).done && !getCellAt(x, y).done) {
                    clist[i] = c;
                    i++;
                    continue;
                }
            }
        }

        Cell[] cs = new Cell[i];
        for (int j = 0; j < i; j++) {
            cs[j] = clist[j];
        }
        return cs;
    }

    public int[] getDrawingLines() {
        removeRedundantWalls();
        int[] l = new int[16*this.width*this.height];
        int i = 0;
        for (int y = 0; y < this.height; y++) {
            for (int x = 0; x < this.width; x++) {
                Cell c = getCellAt(x, y);
                if (c.top) {
                    l[i] = x;
                    l[i+1] = y;
                    l[i+2] = x+1;
                    l[i+3] = y;
                    i += 4;
                }
                if (c.left) {
                    l[i] = x;
                    l[i+1] = y;
                    l[i+2] = x;
                    l[i+3] = y+1;
                    i += 4;
                }
                if (c.bottom) {
                    l[i] = x;
                    l[i+1] = y+1;
                    l[i+2] = x+1;
                    l[i+3] = y+1;
                    i += 4;
                }
                if (c.right) {
                    l[i] = x+1;
                    l[i+1] = y;
                    l[i+2] = x+1;
                    l[i+3] = y+1;
                    i += 4;
                }
            }
        }

        //Gets the array that holds only the lines, the other array will hold many undefined values
        int[] lines = new int[i+1];
        for (int k = 0; k < i; k++) {
            lines[k] = l[k];
        }

        return lines;
    }

    Matrix (int width, int height) {
        this.width = width;
        this.height = height;

        for (int i = 0; i < height; i++) {
            Cell[] row = new Cell[width];
            for (int j = 0; j < width; j++) {
                row[j] = new Cell(j, i);
            }
            this.matrix.add(row);
        }
    }
}