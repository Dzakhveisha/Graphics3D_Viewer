package org.bsuir.graphics.light;

import org.bsuir.graphics.model.Vertex;
import org.bsuir.graphics.service.VectorService;
import org.bsuir.graphics.utils.ProjectConstants;

import java.awt.*;

public class PhongLight {

    private final Vertex theSunVector;
    private final Vertex eye;
    private final VectorService vectorService = new VectorService();

    private static final float ambientStrength = 0.1f;
    private static final float diffuseStrength = 0.8f;
    private static final float specularStrength = 0.8f;
    private static final float specularShines = 16;

    public PhongLight() {

        this.theSunVector = vectorService.normalize(ProjectConstants.THE_SUN);
        this.eye = vectorService.normalize(ProjectConstants.EYE);
    }

    public Color calculatePixelColor(Vertex normal) {

        float[] ambientColorValue = ambientLighting();
        double[] diffuseColorValue = diffuseLighting(normal);
        double[] specularColorValue = specularLighting(normal);
        //int resultColorValue = (int) (ambientColorValue);
        return new Color(
            (int) (ambientColorValue[0] + diffuseColorValue[0] + specularColorValue[0]),
            (int) (ambientColorValue[1] + diffuseColorValue[1] + specularColorValue[1]),
            (int) (ambientColorValue[2] + diffuseColorValue[2] + specularColorValue[2])
        );
    }

    private float[] ambientLighting() {

        float[] color = {200, 0, 0};
        for (int i = 0; i < 3; i++) {
            color[i] *= ambientStrength;
        }
        return color;
    }

    private double[] diffuseLighting(Vertex pixelVector) {

        double[] color = {255, 0, 0};
        double scalar = Math.max(vectorService.scalarMultiply(vectorService.normalize(pixelVector), theSunVector), 0);
        for (int i = 0; i < 3; i++) {
            color[i] = color[i] * scalar * diffuseStrength;
        }
        return color;
    }

    private double[] specularLighting(Vertex pixelVector) {

        double[] color = {255, 255, 255};
        Vertex specularDirection = vectorService.normalize(vectorService.add(theSunVector, eye));
        double specular = vectorService.scalarMultiply(vectorService.normalize(pixelVector), specularDirection);
        double scalar = Math.pow(Math.max(specular, 0), specularShines);
        for (int i = 0; i < 3; i++) {
            color[i] = color[i] * scalar * specularStrength;
        }
        return color;
    }
}
