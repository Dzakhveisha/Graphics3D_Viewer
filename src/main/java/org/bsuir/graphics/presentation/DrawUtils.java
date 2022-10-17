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


    public void drawHorLine(Graphics g, Color color, int xStart, int yStart, float zStart, int xEnd, int yEnd, float zEnd) {
        float zIncrement = (zEnd - zStart) / Math.abs(xEnd - xStart);
        float zDepth = zStart;
        g.setColor(color);
        if (xStart < xEnd) {
            for (int x = xStart; x < xEnd; x++) {
                if (x < SCREEN_WIDTH - 1 && x > 0 && yStart < SCREEN_HEIGHT - 1 && yStart > 0) {
                    if (zBuffer[x][yStart] > zDepth) {
                        zBuffer[x][yStart] = zDepth;
                        g.drawLine(x, yStart, x, yStart);
                    }
                }
                zDepth += zIncrement;
            }
        } else {
            for (int x = xStart; x > xEnd; x--) {
                if (x < SCREEN_WIDTH - 1 && x > 0 && yStart < SCREEN_HEIGHT - 1 && yStart > 0) {
                    if (zBuffer[x][yStart] > zDepth) {
                        zBuffer[x][yStart] = zDepth;
                        g.drawLine(x, yStart, x, yStart);
                    }
                }
                zDepth += zIncrement;

            }
        }
    }

    public void drawLine(Graphics g, Color color, int xStart, int yStart, int xEnd, int yEnd, int zStart, int zEnd) {

        g.setColor(color);
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

        float zDepth = zStart;
        if (x < SCREEN_WIDTH - 1 && x > 0 && y < SCREEN_HEIGHT - 1 && y > 0)
            if (zBuffer[x][y] > zDepth) {
                zBuffer[x][y] = zDepth;
                g.drawLine(x, y, x, y);
            }

        float zIncrement = 0;
        if (el != 0) {
            if (zStart > zEnd) {
                zIncrement = (zStart - zEnd) / el;
            } else
                zIncrement = -(zStart - zEnd) / el;
        }
        for (int t = 0; t < el; t++) {
            err -= es;
            if (err < 0) {
                err += el;
                x += incx;
                y += incy;
                zDepth += zIncrement;
            } else {
                x += pdx;
                y += pdy;
                zDepth += zIncrement;
            }
            if (x < SCREEN_WIDTH - 1 && x > 0 && y < SCREEN_HEIGHT - 1 && y > 0)
                if (zBuffer[x][y] > zDepth) {
                    zBuffer[x][y] = zDepth;
                    g.drawLine(x, y, x, y);
                }
        }
    }

    public Vertex bresenhem_interseption(Vertex vertex1, Vertex vertex2, int curY) {
        int xEnd = Math.round(vertex2.x);
        int xStart = Math.round(vertex1.x);
        int yEnd = Math.round(vertex2.y);
        int yStart = Math.round(vertex1.y);
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

        if (yStart == curY && yEnd == curY) {
            return vertex1;
        }

        if (yStart == curY) {
            return vertex1;
        }
        if (yEnd == curY) {
            return vertex2;
        }

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
            //g.drawLine(x, y, x, y);
            if (curY == y) {
                //return new Vertex(x, y, findDepth());
                if (vertex1.y < vertex2.y) {
                    return new Vertex(x, curY, findDepth(vertex2.z, curY, Math.abs(vertex1.y - vertex2.y), vertex1.z - vertex2.z, vertex2.y));
                } else {
                    return new Vertex(x, curY, findDepth(vertex1.z, curY, Math.abs(vertex1.y - vertex2.y), vertex2.z - vertex1.z, vertex1.y));
                }
            }
        }
        return null;
    }

    public void face_rasterization(Graphics g, Color faceColor, List<Vertex> vertexList) {
        int highestPixel = Math.round(findHigestVertex(vertexList).y);
        int shortestPixel = Math.round(findShortestVertex(vertexList).y);

        List<Vertex> points = new ArrayList<>();
        for (int i = highestPixel; i > shortestPixel; i--) {
            points.clear();
            for (int j = 0; j < vertexList.size(); j++) {
                if (j + 1 == vertexList.size()) {
                    Vertex interseption = bresenhem_interseption(vertexList.get(0), vertexList.get(j), i);
                    if (interseption != null) {
                        points.add(interseption);
                    }
                } else {
                    Vertex interseption = bresenhem_interseption(vertexList.get(j), vertexList.get(j + 1), i);
                    if (interseption != null) {
                        points.add(interseption);
                    }
                }
            }
            if (points.size() == 2) {
                drawHorLine(g, faceColor, Math.round(points.get(0).x), Math.round(points.get(0).y), Math.round(points.get(0).z), Math.round(points.get(1).x), Math.round(points.get(1).y), Math.round(points.get(1).z));
            }
            if (points.size() > 2) {
                if (!points.get(0).equals(points.get(1))) {
                    drawHorLine(g, faceColor, Math.round(points.get(0).x), Math.round(points.get(0).y), Math.round(points.get(0).z), Math.round(points.get(1).x), Math.round(points.get(1).y), Math.round(points.get(1).z));
                } else {
                    if (!points.get(0).equals(points.get(2))) {
                        drawHorLine(g, faceColor, Math.round(points.get(0).x), Math.round(points.get(0).y), Math.round(points.get(0).z), Math.round(points.get(2).x), Math.round(points.get(2).y), Math.round(points.get(2).z));
                    } else {
                        drawHorLine(g, faceColor, Math.round(points.get(0).x), Math.round(points.get(0).y), Math.round(points.get(0).z), Math.round(points.get(3).x), Math.round(points.get(3).y), Math.round(points.get(3).z));
                    }
                }
            }
        }
    }

//    private Vertex interseption(Vertex vertex1, Vertex vertex2, int curY) {
//        if (curY < vertex1.y && curY < vertex2.y || curY > vertex1.y && curY > vertex2.y) {
//            return null;
//        }
//        if (vertex1.x == vertex2.x) {
//            if (vertex1.y < vertex2.y) {
//                return new Vertex(vertex1.x, curY, findDepth(vertex2.z, curY, Math.abs(vertex1.y - vertex2.y), Math.abs(vertex1.z - vertex2.z), vertex2.y));
//            } else {
//                return new Vertex(vertex1.x, curY, findDepth(vertex1.z, curY, Math.abs(vertex1.y - vertex2.y), Math.abs(vertex1.z - vertex2.z), vertex1.y));
//            }
//        }
//        float k = (vertex1.y - vertex2.y) / (vertex1.x - vertex2.x);
//        float b = vertex1.y - k * vertex1.x;
//        float x = (curY - b) / k;
//        if (vertex1.y < vertex2.y) {
//            return new Vertex(x, curY, findDepth(vertex2.z, curY, Math.abs(vertex1.y - vertex2.y), vertex1.z - vertex2.z, vertex2.y));
//        } else {
//            return new Vertex(x, curY, findDepth(vertex1.z, curY, Math.abs(vertex1.y - vertex2.y), vertex2.z - vertex1.z, vertex1.y));
//        }
//    }

    private float findDepth(float startZ, int y, float deltaY, float deltaZ, float maxY) {
        float zIncrement = deltaZ / deltaY;
        float z = startZ + (maxY - y) * zIncrement;
        return z;
    }

    private Vertex findShortestVertex(List<Vertex> vertexList) {
        return vertexList.stream()
                .min(new Comparator<Vertex>() {
                    @Override
                    public int compare(Vertex o1, Vertex o2) {
                        return Float.compare(o1.y, o2.y);
                    }
                })
                .get();
    }

    private Vertex findHigestVertex(List<Vertex> vertexList) {
        return vertexList.stream()
                .max(new Comparator<Vertex>() {
                    @Override
                    public int compare(Vertex o1, Vertex o2) {
                        return Float.compare(o1.y, o2.y);
                    }
                })
                .get();
    }

    private static int sign(int x) {

        return Integer.compare(x, 0);
    }
}
