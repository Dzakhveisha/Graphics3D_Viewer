package org.bsuir.graphics.presentation;

import org.bsuir.graphics.model.Vertex;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public class DrawUtils {

    public void drawLine(Graphics g, int xStart, int yStart, int xEnd, int yEnd) {

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
            g.drawLine(x, y, x, y);
        }
    }

    public void face_rasterization(Graphics g, List<Vertex> vertexList) {
        int highestPixel = (int) findHigestVertexY(vertexList);
        int shortestPixel = (int) findShortestVertexY(vertexList);

        List<Point> points = new ArrayList<>();
        for (int i = highestPixel; i > shortestPixel; i--) {
            points.clear();
            for (int j = 0; j < vertexList.size(); j++) {
                if (j + 1 == vertexList.size()) {
                    Point interseption = interseption(vertexList.get(0), vertexList.get(j), i);
                    if (interseption != null) {
                        points.add(interseption);
                    }
                } else {
                    Point interseption = interseption(vertexList.get(j), vertexList.get(j + 1), i);
                    if (interseption != null) {
                        points.add(interseption);
                    }
                }
            }
            if (points.size() == 2) {
                drawLine(g, points.get(0).x, points.get(0).y, points.get(1).x, points.get(1).y);
            }
        }
    }

    private Point interseption(Vertex vertex1, Vertex vertex2, int i) {
        if (i < vertex1.y && i < vertex2.y || i > vertex1.y && i > vertex2.y || vertex1.y == vertex2.y ) {
            return null;
        }
        if( vertex1.x == vertex2.x){
            return  new Point((int)vertex1.x, i);
        }
        float k = (vertex1.y - vertex2.y) / (vertex1.x - vertex2.x);
        float b = vertex1.y - k * vertex1.x;
        float x = (i - b) / k;
        return new Point((int) x, i);
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
