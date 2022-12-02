package org.bsuir.graphics.mapper;

import org.bsuir.graphics.model.Matrix;
import org.bsuir.graphics.model.Vertex;
import org.bsuir.graphics.utils.ProjectConstants;

public class CoordsMapper {

    private Matrix matrix;
    private final Vertex eye = ProjectConstants.EYE;
    private final Vertex target = ProjectConstants.TARGET;
    private final Vertex up = ProjectConstants.UP;

    private float initRotationX = 0f;
    private float initRotationY = 0f;
    private float initScale = 1;
    private float initPlacementX = 0;
    private float initPlacementY = 0;

    public CoordsMapper() {

        matrix = getTranslationMatrix(initPlacementX, initPlacementY, 0)
            .multiply(getScaleMatrix(initScale, initScale, initScale))
            .multiply(rotateXMatrix(initRotationX))
            .multiply(rotateYMatrix(initRotationY))
            .multiply(rotateZMatrix(0));
    }

    public Vertex fromModelSpaceToViewPort(Vertex vertex) {

        Vertex resultVertex;
        resultVertex = fromModelSpaceToWorldSpace(vertex);
        resultVertex = fromWorldSpaceToObserverSpace(resultVertex);
        resultVertex = fromObserverSpaceToProjectionSpace(resultVertex);
        resultVertex = fromProjectionSpaceToViewPort(resultVertex);

        return resultVertex;
    }

    public Vertex fromModelSpaceToWorldSpace(Vertex vertex) {

        return matrix.multiply(vertex);
    }

    private Vertex fromWorldSpaceToObserverSpace(Vertex vertex) {

        Vertex zAxis = normalize(subtraction(eye, target));
        Vertex xAxis = normalize(multiply(up, zAxis));

        Matrix transform = getTransformMatrix(xAxis, up, zAxis);
        return transform.multiply(vertex);
    }

    private Vertex fromObserverSpaceToProjectionSpace(Vertex Vertex) {

        float width = 1280f;
        float height = 720f;
        float zNear = 720f;
        float zFar = 2000f;

        Matrix matrix = getPerspectiveMatrix(width, height, zNear, zFar);
        Vertex result = matrix.multiply(Vertex);
        result.wAd = result.w;
        for (int i = 0; i < 4; i++) {
            result.setElement(i, result.getElement(i) / result.getElement(3));
        }
        return result;
    }

    private Vertex fromProjectionSpaceToViewPort(Vertex Vertex) {

        float width = 1280f;
        float height = 720f;

        Matrix matrix = getWindowMatrix(width, height);
        Vertex result = matrix.multiply(Vertex);
        for (int i = 0; i < 3; i++) {
            result.setElement(i, result.getElement(i));
        }
        return result;

    }

    private Matrix getWindowMatrix(float width, float height) {

        Matrix matrix = new Matrix();
        matrix.setElement(0, 0, width / 2);
        matrix.setElement(0, 3, width / 2);
        matrix.setElement(1, 1, -height / 2);
        matrix.setElement(1, 3, height / 2);
        matrix.setElement(2, 2, 1);
        matrix.setElement(3, 3, 1);
        return matrix;
    }

    private Matrix getTranslationMatrix(float x, float y, float z) {

        Matrix matrix = new Matrix();
        matrix.setElement(0, 3, x);
        matrix.setElement(1, 3, y);
        matrix.setElement(2, 3, z);
        matrix.setElement(3, 3, 1);
        return matrix;
    }

    private Matrix getScaleMatrix(float x, float y, float z) {

        Matrix matrix = new Matrix();
        matrix.setElement(0, 0, x);
        matrix.setElement(1, 1, y);
        matrix.setElement(2, 2, z);
        matrix.setElement(3, 3, 1);
        return matrix;
    }

    public Matrix rotateXMatrix(double angle) {

        Matrix rotationMatrix = new Matrix();

        float cosAngle = (float) Math.cos(angle);
        float sinAngle = (float) Math.sin(angle);

        rotationMatrix.setElement(1, 1, cosAngle);
        rotationMatrix.setElement(1, 2, -sinAngle);
        rotationMatrix.setElement(2, 1, sinAngle);
        rotationMatrix.setElement(2, 2, cosAngle);

        return rotationMatrix;
    }

    public Matrix rotateYMatrix(double angle) {

        Matrix rotationMatrix = new Matrix();

        float cosAngle = (float) Math.cos(angle);
        float sinAngle = (float) Math.sin(angle);

        rotationMatrix.setElement(0, 0, cosAngle);
        rotationMatrix.setElement(0, 2, sinAngle);
        rotationMatrix.setElement(1, 1, 1);
        rotationMatrix.setElement(2, 0, -sinAngle);
        rotationMatrix.setElement(2, 2, cosAngle);
        rotationMatrix.setElement(3, 3, 1);

        return rotationMatrix;
    }

    public Matrix rotateZMatrix(double angle) {

        Matrix rotationMatrix = new Matrix();

        float cosAngle = (float) Math.cos(angle);
        float sinAngle = (float) Math.sin(angle);

        rotationMatrix.setElement(0, 0, cosAngle);
        rotationMatrix.setElement(0, 1, sinAngle);
        rotationMatrix.setElement(1, 0, -sinAngle);
        rotationMatrix.setElement(1, 1, cosAngle);

        return rotationMatrix;
    }

    private Matrix getTransformMatrix(Vertex xAxis, Vertex yAxis, Vertex zAxis) {

        Matrix rotationMatrix = new Matrix();
        rotationMatrix.setElement(0, 0, xAxis.getElement(0));
        rotationMatrix.setElement(0, 1, xAxis.getElement(1));
        rotationMatrix.setElement(0, 2, xAxis.getElement(2));
        rotationMatrix.setElement(0, 3, -scalarSubstraction(xAxis, eye));

        rotationMatrix.setElement(1, 0, yAxis.getElement(0));
        rotationMatrix.setElement(1, 1, yAxis.getElement(1));
        rotationMatrix.setElement(1, 2, yAxis.getElement(2));
        rotationMatrix.setElement(1, 3, -scalarSubstraction(yAxis, eye));

        rotationMatrix.setElement(2, 0, zAxis.getElement(0));
        rotationMatrix.setElement(2, 1, zAxis.getElement(1));
        rotationMatrix.setElement(2, 2, zAxis.getElement(2));
        rotationMatrix.setElement(2, 3, -scalarSubstraction(zAxis, eye));

        rotationMatrix.setElement(3, 3, 1);

        return rotationMatrix;
    }

    private Matrix getPerspectiveMatrix(float width, float height, float zNear, float zFar) {

        int fov = 75;
        double aspect = width / height;
        Matrix matrix = new Matrix();
        matrix.setElement(0, 0, (float) (1 / (aspect * Math.tan(fov / 2))));
        matrix.setElement(1, 1, (float) (1 / Math.tan(fov / 2)));
        matrix.setElement(2, 2, zFar / (zNear - zFar));
        matrix.setElement(2, 3, (zNear * zFar) / (zNear - zFar));
        matrix.setElement(3, 2, -1);
        return matrix;
    }

    private Vertex subtraction(Vertex v1, Vertex v2) {

        Vertex result = new Vertex();
        for (int i = 0; i < 4; i++) {
            result.setElement(i, v1.getElement(i) - v2.getElement(i));
        }
        return result;
    }

    private float scalarSubstraction(Vertex v1, Vertex v2) {

        return (v1.getElement(0) * v2.getElement(0)
            + v1.getElement(1) * v2.getElement(1) + v1.getElement(2) * v2.getElement(2));
    }

    private Vertex multiply(Vertex v1, Vertex v2) {

        Vertex result = new Vertex();
        result.setElement(0, v1.getElement(1) * v2.getElement(2) - v1.getElement(2) * v2.getElement(1));
        result.setElement(1, v1.getElement(2) * v2.getElement(0) - v1.getElement(0) * v2.getElement(2));
        result.setElement(2, v1.getElement(0) * v2.getElement(1) - v1.getElement(1) * v2.getElement(0));
        result.setElement(3, 1);
        return result;
    }

    private Vertex normalize(Vertex v) {

        float length = (float) Math.sqrt(v.getElement(0) * v.getElement(0)
            + v.getElement(1) * v.getElement(1) + v.getElement(2) * v.getElement(2));
        v.setElement(0, v.getElement(0) * 1 / length);
        v.setElement(1, v.getElement(1) * 1 / length);
        v.setElement(2, v.getElement(2) * 1 / length);
        return v;
    }

    public void move(int deltaX, int deltaY) {

        initPlacementX += deltaX;
        initPlacementY -= deltaY;

        matrix = getTranslationMatrix(initPlacementX, initPlacementY, 0)
            .multiply(getScaleMatrix(initScale, initScale, initScale))
            .multiply(rotateXMatrix(initRotationX))
            .multiply(rotateYMatrix(initRotationY))
            .multiply(rotateZMatrix(0));
    }

    public void scale(double delay) {

        double zoom = 0.05;

        if (delay < 0) {
            initScale += zoom;
        } else {
            initScale -= zoom;
        }

        matrix = getTranslationMatrix(initPlacementX, initPlacementY, 0)
            .multiply(getScaleMatrix(initScale, initScale, initScale))
            .multiply(rotateXMatrix(initRotationX))
            .multiply(rotateYMatrix(initRotationY))
            .multiply(rotateZMatrix(0));
    }

    public void rotate(double xOffset, double yOffset) {

        initRotationX -= (float) xOffset;
        initRotationY += (float) yOffset;

        matrix = getTranslationMatrix(initPlacementX, initPlacementY, 0)
            .multiply(getScaleMatrix(initScale, initScale, initScale))
            .multiply(rotateXMatrix(initRotationX))
            .multiply(rotateYMatrix(initRotationY))
            .multiply(rotateZMatrix(0));
    }
}
