package org.bsuir.graphics.presentation;

import org.bsuir.graphics.light.PhongLight;
import org.bsuir.graphics.mapper.CoordsMapper;
import org.bsuir.graphics.model.ModelObject;
import org.bsuir.graphics.model.Texture;
import org.bsuir.graphics.model.Vertex;
import org.bsuir.graphics.service.VectorService;
import org.bsuir.graphics.utils.ProjectConstants;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class DrawUtils {

    private static final int SCREEN_WIDTH = ProjectConstants.SCREEN_WIDTH;
    private static final int SCREEN_HEIGHT = ProjectConstants.SCREEN_HEIGHT;
    private float[][] zBuffer = new float[SCREEN_WIDTH][SCREEN_HEIGHT];

    private final PhongLight phongLight;

    private final VectorService vectorService = new VectorService();

    public DrawUtils(CoordsMapper mapper) {

        phongLight = new PhongLight(mapper);
    }

    public void initBuffer() {

        for (
            float[] floats : zBuffer) {
            Arrays.fill(floats, 1000000000);
        }

    }

    public void drawHorLine(
        Graphics g,
        ModelObject object,
        Vertex normalP1,
        Vertex normalP2,
        Texture texture1,
        Texture texture2,
        Vertex vertex1,
        Vertex vertex2,
        int xStart,
        int yStart,
        float zStart,
        int xEnd,
        int yEnd,
        float zEnd
    ) {

        float zIncrement = (zEnd - zStart) / Math.abs(xEnd - xStart);
        float zDepth = zStart;
        if (xStart < xEnd) {
            for (int x = xStart; x < xEnd; x++) {
                if (x < SCREEN_WIDTH - 1 && x > 0 && yStart < SCREEN_HEIGHT - 1 && yStart > 0) {
                    if (zBuffer[x][yStart] > zDepth) {
                        zBuffer[x][yStart] = zDepth;

                        double interpolation = getInterpolationCoef(new Vertex(x, yStart, zDepth),
                            new Vertex(xStart, yStart, zStart),
                            new Vertex(xEnd, yEnd, zEnd));

                        Texture textile = getPolygonTextiles(texture1, texture2, vertex1.wAd, vertex2.wAd, interpolation);

                        var pixelNormal =
                            calcNormal(
                                new Vertex(x, yStart, zDepth),
                                new Vertex(xStart, yStart, zStart),
                                new Vertex(xEnd, yEnd, zEnd),
                                normalP1,
                                normalP2
                            );

                        g.setColor(phongLight.calculatePixelColor(object, pixelNormal, new Vertex(x, yStart, zDepth),
                            textile));
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

                        double interpolation = getInterpolationCoef(new Vertex(x, yStart, zDepth),
                            new Vertex(xStart, yStart, zStart),
                            new Vertex(xEnd, yEnd, zEnd));

                        Texture textile = getPolygonTextiles(texture1, texture2, vertex1.wAd, vertex2.wAd, interpolation);

                        var pixelNormal =
                            calcNormal(
                                new Vertex(x, yStart, zDepth),
                                new Vertex(xStart, yStart, zStart),
                                new Vertex(xEnd, yEnd, zEnd),
                                normalP1,
                                normalP2
                            );
                        g.setColor(phongLight.calculatePixelColor(object, pixelNormal, new Vertex(x, yStart, zDepth),
                            textile));
                        g.drawLine(x, yStart, x, yStart);
                    }
                }
                zDepth += zIncrement;
            }
        }
    }

    public void drawLine(
        Graphics g,
        ModelObject object,
        Vertex normalP1,
        Vertex normalP2,
        Texture texture1,
        Texture texture2,
        int xStart,
        int yStart,
        int xEnd,
        int yEnd,
        int zStart,
        int zEnd
    ) {

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

                double interpolation = getInterpolationCoef(new Vertex(x, yStart, zDepth),
                    new Vertex(xStart, yStart, zStart),
                    new Vertex(xEnd, yEnd, zEnd));

                Texture textile = getPolygonTextiles(texture1, texture2, zStart, zEnd, interpolation);

                var pixelNormal =
                    calcNormal(
                        new Vertex(x, y, zDepth),
                        new Vertex(xStart, yStart, zStart),
                        new Vertex(xEnd, yEnd, zEnd),
                        normalP1,
                        normalP2
                    );

                g.setColor(phongLight.calculatePixelColor(object, pixelNormal, new Vertex(x, y, zDepth), textile));
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

                    double interpolation = getInterpolationCoef(new Vertex(x, y, zDepth),
                        new Vertex(xStart, yStart, zStart),
                        new Vertex(xEnd, yEnd, zEnd));

                    Texture textile = getPolygonTextiles(texture1, texture2, zStart, zEnd, interpolation);

                    var pixelNormal =
                        calcNormal(
                            new Vertex(x, y, zDepth),
                            new Vertex(xStart, yStart, zStart),
                            new Vertex(xEnd, yEnd, zEnd),
                            normalP1,
                            normalP2
                        );

                    g.setColor(phongLight.calculatePixelColor(object, pixelNormal, new Vertex(x, y, zDepth), textile));
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
                    return new Vertex(x, curY, findDepth(vertex2.z, curY, Math.abs(vertex1.y - vertex2.y),
                        vertex1.z - vertex2.z, vertex2.y), 1, vertex1.wAd);
                } else {
                    return new Vertex(x, curY, findDepth(vertex1.z, curY, Math.abs(vertex1.y - vertex2.y),
                        vertex2.z - vertex1.z, vertex1.y), 1, vertex2.wAd);
                }
            }
        }
        return null;
    }

    public void face_rasterization(
        Graphics g,
        ModelObject object,
        List<Vertex> vertexList,
        List<Vertex> normalList,
        List<Texture> textureList
    ) {

        int highestPixel = Math.round(findHigestVertex(vertexList).y);
        int shortestPixel = Math.round(findShortestVertex(vertexList).y);

        List<Vertex> points = new ArrayList<>();
        List<Vertex> pointsNormals = new ArrayList<>();
        List<Texture> pointsTextures = new ArrayList<>();
        for (int i = highestPixel; i > shortestPixel; i--) {
            points.clear();
            pointsNormals.clear();
            pointsTextures.clear();
            for (int j = 0; j < vertexList.size(); j++) {
                if (j + 1 == vertexList.size()) {
                    Vertex interseption = bresenhem_interseption(vertexList.get(0), vertexList.get(j), i);
                    if (interseption != null) {
                        points.add(interseption);

                        double interpolation = getInterpolationCoef(
                            interseption, vertexList.get(0), vertexList.get(j)
                        );

                        Texture textile = getPolygonTextiles(textureList.get(0), textureList.get(j),
                            vertexList.get(0).z, vertexList.get(j).z, interpolation);

                        pointsTextures.add(textile);
                        pointsNormals.add(
                            calcNormal(interseption, vertexList.get(0), vertexList.get(j), normalList.get(0),
                                normalList.get(j))
                        );
                    }
                } else {
                    Vertex interseption = bresenhem_interseption(vertexList.get(j), vertexList.get(j + 1), i);
                    if (interseption != null) {
                        points.add(interseption);

                        double interpolation = getInterpolationCoef(
                            interseption, vertexList.get(j), vertexList.get(j + 1)
                        );

                        Texture textile = getPolygonTextiles(textureList.get(j), textureList.get(j + 1),
                            vertexList.get(j).z, vertexList.get(j + 1).z, interpolation);

                        pointsTextures.add(textile);

                        pointsNormals.add(
                            calcNormal(interseption, vertexList.get(j), vertexList.get(j + 1), normalList.get(j),
                                normalList.get(j + 1))
                        );
                    }
                }
            }
            if (points.size() == 2) {
                drawHorLine(
                    g,
                    object,
                    pointsNormals.get(0),
                    pointsNormals.get(1),
                    pointsTextures.get(0),
                    pointsTextures.get(1),
                    points.get(0),
                    points.get(1),
                    Math.round(points.get(0).x),
                    Math.round(points.get(0).y),
                    Math.round(points.get(0).z),
                    Math.round(points.get(1).x),
                    Math.round(points.get(1).y),
                    Math.round(points.get(1).z));
            }
            if (points.size() > 2) {
                if (!points.get(0).equals(points.get(1))) {
                    drawHorLine(g,
                        object,
                        pointsNormals.get(0),
                        pointsNormals.get(1),
                        pointsTextures.get(0),
                        pointsTextures.get(1),
                        points.get(0),
                        points.get(1),
                        Math.round(points.get(0).x),
                        Math.round(points.get(0).y),
                        Math.round(points.get(0).z),
                        Math.round(points.get(1).x),
                        Math.round(points.get(1).y),
                        Math.round(points.get(1).z));
                } else {
                    if (!points.get(0).equals(points.get(2))) {
                        drawHorLine(g,
                            object,
                            pointsNormals.get(0),
                            pointsNormals.get(2),
                            pointsTextures.get(0),
                            pointsTextures.get(2),
                            points.get(0),
                            points.get(2),
                            Math.round(points.get(0).x),
                            Math.round(points.get(0).y),
                            Math.round(points.get(0).z),
                            Math.round(points.get(2).x),
                            Math.round(points.get(2).y),
                            Math.round(points.get(2).z));
                    } else {
                        drawHorLine(g,
                            object,
                            pointsNormals.get(0),
                            pointsNormals.get(3),
                            pointsTextures.get(0),
                            pointsTextures.get(3),
                            points.get(0),
                            points.get(3),
                            Math.round(points.get(0).x),
                            Math.round(points.get(0).y),
                            Math.round(points.get(0).z),
                            Math.round(points.get(3).x),
                            Math.round(points.get(3).y),
                            Math.round(points.get(3).z));
                    }
                }
            }
        }
    }

    private float findDepth(float startZ, int y, float deltaY, float deltaZ, float maxY) {

        float zIncrement = deltaZ / deltaY;
        float z = startZ + (maxY - y) * zIncrement;
        return z;
    }

    private Vertex findShortestVertex(List<Vertex> vertexList) {

        return vertexList.stream()
            .min((o1, o2) -> Float.compare(o1.y, o2.y))
            .get();
    }

    private Vertex findHigestVertex(List<Vertex> vertexList) {

        return vertexList.stream()
            .max((o1, o2) -> Float.compare(o1.y, o2.y))
            .get();
    }

    private static int sign(int x) {

        return Integer.compare(x, 0);
    }

    private double getInterpolationCoef(
        Vertex point,
        Vertex pointStart,
        Vertex pointFinish
    ) {

        return vectorService.getLength(
            vectorService.subtract(point, pointStart)
        ) / vectorService.getLength(
            vectorService.subtract(pointFinish, pointStart)
        );
    }

    private double getInterpolationCoef(
        Texture point,
        Texture pointStart,
        Texture pointFinish
    ) {

        return vectorService.getLength(
            vectorService.subtract(point, pointStart)
        ) / vectorService.getLength(
            vectorService.subtract(pointFinish, pointStart)
        );
    }

    public Texture getPolygonTextiles(Texture texture1, Texture texture2, float z1, float z2, double interpolation) {

        double z = ((1 - interpolation) / z1 + interpolation / z2);
        double textureX = ((1 - interpolation) * texture1.u / z1 + interpolation * texture2.u / z2) / z;
        double textureY = ((1 - interpolation) * texture1.v / z1 + interpolation * texture2.v / z2) / z;
        /*double textureX = ((1 - interpolation) * texture1.u) + (interpolation * texture2.u);
        double textureY = ((1 - interpolation) * texture1.v) + (interpolation * texture2.v);*/
        return new Texture((float) textureX, (float) textureY, 1);
    }

    private Vertex calcNormal(
        Vertex point,
        Vertex pointStart,
        Vertex pointFinish,
        Vertex normalStart,
        Vertex normalEnd
    ) {

        double s = vectorService.getLength(
            vectorService.subtract(point, pointStart)
        ) / vectorService.getLength(
            vectorService.subtract(pointFinish, pointStart)
        );

        return vectorService.add(
            vectorService.multiply((float) s, normalEnd),
            vectorService.multiply((float) (1 - s), normalStart)
        );
    }
}
