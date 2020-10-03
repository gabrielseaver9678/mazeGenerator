/*
MAZE GENERATOR
Author: Gabriel Seaver
Date: 10/26/2019 and 10/27/2019
Description: A few years ago, I created a very similar maze generator program on Khan Academy using processing.js. I have now
recreated the maze generator program in Java for Windows. This uses the hunt-and-kill algorithm. 
https://www.khanacademy.org/computer-programming/maze-generator-2/6743924800159744
*/



package mazes;

import java.util.Arrays;
import java.util.ArrayList;

import java.util.Scanner;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.ImageIcon;

class Maze {
    
    int width;
    int height;
    Matrix mazeMatrix;
    Scanner input;
    int horizontalBias;

    Maze () {
        this.width = 0;
        this.height = 0;
        //Scanner Setup
        this.input = new Scanner(System.in);
        this.horizontalBias = 101;
    }

    //Sets width
    public void setWidth() {
        if (this.height != 0) {
            //Height already set, gives user the option to set same or different width
            String ans = "";
            while (!ans.equals("y") && !ans.equals("n")) {
                System.out.print("Would you like the width and height to be the same, at " + this.height + " units? <y|n>: ");
                ans = this.input.next();
            }
            if (ans.equals("y")) {
                this.width = this.height;
                System.out.println("Maze width set to " + this.width);
                return;
            }
        }
        //Height not already set, or user wants to set different width and height
        System.out.println("Please enter the desired maze width.");
        while (this.width < 5 || this.width > 300) {
            System.out.print("Maze width [5, 300]: ");
            this.width = input.nextInt();
            if (this.width < 5 || this.width > 300) {
                System.out.println("Please enter a width in the range [5, 300].");
            }
        }
        //Outputs width    
        System.out.println("Maze width set to " + this.width);
    }

    //Sets height
    public void setHeight() {
        if (this.width != 0) {
            //Width already set, gives user the option to set same or different height
            String ans = "";
            while (!ans.equals("y") && !ans.equals("n")) {
                System.out.print("Would you like the width and height to be the same, at " + this.width + " units? <y|n>: ");
                ans = this.input.next();
            }
            if (ans.equals("y")) {
                this.height = this.width;
                System.out.println("Maze height set to " + this.height);
                return;
            }
        }
        //Width not already set, or user wants to set different width and height
        System.out.println("Please enter the desired maze height.");
        while (this.height < 5 || this.height > 300) {
            System.out.print("Maze height [5, 300]: ");
            this.height = input.nextInt();
            if (this.height < 5 || this.height > 300) {
                System.out.println("Please enter a height in the range [5, 300].");
            }
        }
        //Outputs height
        System.out.println("Maze height set to " + this.height);
    }

    //Sets bias
    public void setBias() {
        String ans = "";
        while (!ans.equals("vert") && !ans.equals("hori") && !ans.equals("n")) {
            System.out.print("Would you like there to be a horizontal or vertical bias? <vert|hori|n>: ");
            ans = this.input.next();
        }
        if (ans.equals("n")) {
            this.horizontalBias = 50;
            return;
        }
        System.out.println("Please enter the desired horizontal/vertical bias.");
        while (!(this.horizontalBias >= 0) || !(this.horizontalBias <= 100)) {
            if (ans.equals("vert")) {
                System.out.print("Vertical bias, default is 50% [0, 100]: ");
                this.horizontalBias = 100 - input.nextInt();
            } else {
                System.out.print("Horizontal bias, default is 50% [0, 100]: ");
                this.horizontalBias = input.nextInt();
            }
        }
        System.out.println("Vertical bias set to "+(100-this.horizontalBias)+"%, horizontal bias set to "+this.horizontalBias+"%");
    }

    //Generates maze
    public void generate() {
        this.mazeMatrix = new Matrix(this.width, this.height);
        boolean isDone = false;

        int x = 0;
        int y = 0;

        int huntY = 0;

        String stage = "kill";

        while (!isDone) {
            if (stage.equals("kill")) {
                int[] m = this.mazeMatrix.getRandomVelocity(x, y, this.horizontalBias);
                this.mazeMatrix.getCellAt(x, y).done = true;
                if (m.length == 1) {
                    stage = "hunt";
                } else {
                    String mov;
                    if (m[0] != 0) { //Moved either right or left
                        mov = "right";
                        if (m[0] == -1) {
                            mov = "left";
                        }
                    } else {
                        mov = "bottom";
                        if (m[1] == -1) {
                            mov = "top";
                        }
                    }
                    this.mazeMatrix.setCellBarrier(x, y, mov, false);
                    x += m[0];
                    y += m[1];
                }
            } else if (stage.equals("hunt")) {
                boolean huntSuccessful = false;
                while (!huntSuccessful) {
                    Cell[] m = this.mazeMatrix.getConnectedCellsOnRow(huntY);
                    
                    
                    if (m.length == 0) { //Hunt unsuccessful. Need to move onto next y.
                        huntY ++;
                        if (huntY >= this.height) { //The hunt y is off the grid. Maze generation is done.
                            huntSuccessful = true;
                            isDone = true;
                            stage = "Complete";
                            System.out.println("Maze Completed");
                        }
                    } else { //Hunt successful. Need to choose random cell from list, then random wall connected to finished cell to break
                        huntSuccessful = true;
                        int rand = (int)(Math.floor(Math.random()*m.length));
                        Cell cell = m[rand];

                        //Now we need to make a list of the walls on this cell connected to a completed cell, then break one
                        int[] walls = {-1, 0, 1, 0, 0, 1, 0, -1};
                        String[] wallNames = {"left", "right", "bottom", "top"};

                        String[] possibleWallNames = new String[4];

                        int j = 0; //The counter going through possibilities. i is the counter going through the original walls and names
                        for (int i = 0; i < 8; i+=2) {
                            if (walls[i]+cell.x >= 0 && walls[i]+cell.x < this.width && walls[i+1]+cell.y >= 0 && walls[i+1]+cell.y < this.height) {
                                if (this.mazeMatrix.getCellAt(walls[i]+cell.x, walls[i+1]+cell.y).done) {
                                    possibleWallNames[j/2] = wallNames[i/2];
                                    j+=2;
                                }
                            }
                        }

                        //Now we need to choose a random number between 0 and (j/2)-1. For ex., if all are actual options j=8, j/2=4, (j/2)-1=3
                        rand = (int)(Math.floor(Math.random()*(j/2)));
                        String wallName = possibleWallNames[rand];

                        //Now we need to break the correct wall
                        this.mazeMatrix.setCellBarrier(cell.x, cell.y, wallName, false);
                        x = cell.x;
                        y = cell.y;
                        stage = "kill";
                    }
                }
            }
        }

        //Start and end
        this.mazeMatrix.setCellBarrier(0, 0, "top", false);
        this.mazeMatrix.setCellBarrier(this.width-1, this.height-1, "bottom", false);
    }

    //Outputs maze
    public void drawMaze() {
        //JFrame window setup
        JFrame frame = new JFrame("Maze Generator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        MazePanel mPanel = new MazePanel(this.mazeMatrix.getDrawingLines(), this.width, this.height);

        //Finds the correct width and height
        int ratio = this.width/this.height;
        int w = 600;
        if (this.width < this.height) {
            w = this.width*(600/this.height);
        }
        int h = 600;
        if (this.height < this.width) {
            h = this.height*(600/this.width);
        }
        w += 40;
        h += 40;

        mPanel.setPreferredSize(new Dimension(w, h));
        frame.getContentPane().add(mPanel);
        frame.setLocation(200, 80);
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        //Intro
        System.out.println("Welcome to the maze creator program!");
        //Create Maze
        Maze maze = new Maze();
        maze.setWidth();
        maze.setHeight();
        maze.setBias();
        String st = "and horizontal-vertical bias at "+maze.horizontalBias+"%-"+(100-maze.horizontalBias)+"%...";
        System.out.println("Proceeding to create a maze with dimensions "+maze.width+"x"+maze.height+" " + st);
        maze.generate();
        maze.drawMaze();

    }
}