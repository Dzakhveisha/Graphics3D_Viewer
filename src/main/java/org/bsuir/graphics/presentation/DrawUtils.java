package org.bsuir.graphics.presentation;

import org.bsuir.graphics.model.Vertex;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;


public class DrawUtils {

    private static final int SCREEN_WIDTH = 1280;
    private static final int SCREEN_HEIGHT = 720;

    private float[][] zBuffer = new float[SCREEN_WIDTH][SCREEN_HEIGHT];


    public void initBuffer() {
        for (
                float[] floats : zBuffer) {
            Arrays.fill(floats, 1000000000);
        }

    }


    public void drawLine(Graphics g, Color color, int xStart, int yStart, float zStart, int xEnd, int yEnd, float zEnd) {
        float zIncrement = (zEnd - zStart) / (xEnd - xStart);
        float zDepth = zStart;
        g.setColor(color);
        if (xStart < xEnd) {
            for (int x = xStart; x < xEnd; x++) {
                if (zBuffer[x][yStart] > zDepth && x < SCREEN_WIDTH && x > 0 && yStart < SCREEN_WIDTH && yStart > 0) {
                    zBuffer[x][yStart] = zDepth;
                    g.drawLine(x, yStart, x, yStart);
                }
                zDepth += zIncrement;
            }
        } else {
            for (int x = xStart; x > xEnd; x--) {
                if (zBuffer[x][yStart] > zDepth && x < SCREEN_WIDTH && x > 0 && yStart < SCREEN_WIDTH && yStart > 0) {
                    zBuffer[x][yStart] = zDepth;
                    g.drawLine(x, yStart, x, yStart);
                }
                zDepth += zIncrement;

            }
        }
    }

    public void drawLine(Graphics g, Color color, int xStart, int yStart, int xEnd, int yEnd) {
        int x;
        int y;
        int dx;
        int dy;
        int incx;
        int incy;
        int pdx;
        int pdy;
        int es;
        int el;
        int err;

        dx = xEnd - xStart;
        dy = yEnd - yStart;

        incx = sign(dx);
        incy = sign(dy);

        if (dx < 0) {
            dx = -dx;
        }

        if (dy < 0) {
            dy = -dy;
        }

        if (dx > dy) {
            pdx = incx;
            pdy = 0;
            es = dy;
            el = dx;
        } else {
            pdx = 0;
            pdy = incy;
            es = dx;
            el = dy;
        }

        x = xStart;
        y = yStart;
        err = el / 2;

        for (int t = 0; t < el; t++) {
            err -= es;
            if (err < 0) {
                err += el;
                x += incx;
                y += incy;
            } else {
                x += pdx;
                y += pdy;
            }
            g.setColor(color);
            g.drawLine(x, y, x, y);
        }
    }

    public void face_rasterization(Graphics g, Color faceColor, List<Vertex> vertexList) {
        int highestPixel = (int) findHigestVertexY(vertexList);
        int shortestPixel = (int) findShortestVertexY(vertexList);
        int deltaY = highestPixel - shortestPixel;

        List<Vertex> points = new ArrayList<>();
        for (int i = highestPixel; i > shortestPixel; i--) {
            points.clear();
            for (int j = 0; j < vertexList.size(); j++) {
                if (j + 1 == vertexList.size()) {
                    Vertex interseption = interseption(vertexList.get(0), vertexList.get(j), i, deltaY, highestPixel);
                    if (interseption != null) {
                        points.add(interseption);
                    }
                } else {
                    Vertex interseption = interseption(vertexList.get(j), vertexList.get(j + 1), i, deltaY, highestPixel);
                    if (interseption != null) {
                        points.add(interseption);
                    }
                }
            }
            if (points.size() == 2) {
                drawLine(g, faceColor, (int) points.get(0).x, (int) points.get(0).y, (int) points.get(0).z, (int) points.get(1).x, (int) points.get(1).y, (int) points.get(1).z);
            }
        }
    }

    private Vertex interseption(Vertex vertex1, Vertex vertex2, int curY, int deltaY, int highestPixel) {
        if (curY < vertex1.y && curY < vertex2.y || curY > vertex1.y && curY > vertex2.y || vertex1.y == vertex2.y) {
            return null;
        }
        if (vertex1.x == vertex2.x) {
            if (vertex1.y < vertex2.y) {
                return new Vertex(vertex1.x, curY, findDepth(vertex1.z, curY, highestPixel, deltaY, Math.abs(vertex1.z - vertex2.z)));
            } else {
                return new Vertex(vertex1.x, curY, findDepth(vertex2.z, curY, highestPixel, deltaY, Math.abs(vertex1.z - vertex2.z)));
            }
        }
        float k = (vertex1.y - vertex2.y) / (vertex1.x - vertex2.x);
        float b = vertex1.y - k * vertex1.x;
        float x = (curY - b) / k;
        if (vertex1.y < vertex2.y) {
            return new Vertex(x, curY, findDepth(vertex1.z, curY, highestPixel, deltaY, Math.abs(vertex1.z - vertex2.z)));
        } else {
            return new Vertex(x, curY, findDepth(vertex2.z, curY, highestPixel, deltaY, Math.abs(vertex1.z - vertex2.z)));
        }
    }

    private float findDepth(float startZ, int y, int highestPixel, float deltaY, float deltaZ) {
        float zIncrement = deltaZ / deltaY;
        float z = startZ + (highestPixel - y) * zIncrement;
        return z;
    }

    private float findShortestVertexY(List<Vertex> vertexList) {
        return vertexList.stream()
                .min(new Comparator<Vertex>() {
                    @Override
                    public int compare(Vertex o1, Vertex o2) {
                        return Float.compare(o1.y, o2.y);
                    }
                })
                .get()
                .y;
    }

    private float findHigestVertexY(List<Vertex> vertexList) {
        return vertexList.stream()
                .max(new Comparator<Vertex>() {
                    @Override
                    public int compare(Vertex o1, Vertex o2) {
                        return Float.compare(o1.y, o2.y);
                    }
                })
                .get()
                .y;
    }

    private static int sign(int x) {

        return Integer.compare(x, 0);
    }
}
