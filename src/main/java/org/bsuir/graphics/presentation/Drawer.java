package org.bsuir.graphics.presentation;

import org.bsuir.graphics.model.Face;
import org.bsuir.graphics.model.Vertex;
import org.bsuir.graphics.utils.ProjectConstants;

import java.awt.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class Drawer {

    private float[][] zBuffer = new float[ProjectConstants.SCREEN_WIDTH][ProjectConstants.SCREEN_HEIGHT];

    public void initBuffer() {

        for (float[] floats : zBuffer) {
            Arrays.fill(floats, 1000000000);
        }
    }

    public void drawPolygon(Graphics g, List<Vertex> vertices) {

        Vertex shortestVertex = findShortestVertex(vertices);
        Vertex highestVertex = findHighestVertex(vertices);
        Vertex leftestVertex = findLeftestVertex(vertices);
        Vertex rightestVertex = findRightestVertex(vertices);

        int deltaY = (int) Math.abs(highestVertex.y - shortestVertex.y);
        int deltaX = (int) Math.abs(rightestVertex.x - leftestVertex.x);

        fillPolygonLine(deltaX, deltaY);


        boolean inside = true;
        for (Vertex vertex : vertices) {

        }

        for(int i = 0; i < vertices.size() - 1; i++) {
            if (i == vertices.size() - 1) {

            }
        }
    }

    public void fillPolygonLine(int deltaX, int deltaY) {

        for (int y = 0; y < deltaY; y++) {

        }
    }

    private boolean edgeFunction(Vertex a, Vertex b, Vertex c) {

        return ((c.x - a.x) * (b.y - a.y) - (c.y - a.y) * (b.x - a.x) >= 0);
    }

    private Vertex findShortestVertex(List<Vertex> vertexList) {

        return vertexList.stream()
            .min((o1, o2) -> Float.compare(o1.y, o2.y))
            .get();
    }

    private Vertex findHighestVertex(List<Vertex> vertexList) {

        return vertexList.stream()
            .max((o1, o2) -> Float.compare(o1.y, o2.y))
            .get();
    }

    private Vertex findRightestVertex(List<Vertex> vertexList) {

        return vertexList.stream()
            .max((o1, o2) -> Float.compare(o1.x, o2.x))
            .get();
    }

    private Vertex findLeftestVertex(List<Vertex> vertexList) {

        return vertexList.stream()
            .min((o1, o2) -> Float.compare(o1.x, o2.x))
            .get();
    }

    private static int sign(int x) {

        return Integer.compare(x, 0);
    }
}
