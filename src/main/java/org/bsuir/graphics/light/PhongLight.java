package org.bsuir.graphics.light;

import org.bsuir.graphics.mapper.CoordsMapper;
import org.bsuir.graphics.model.ModelObject;
import org.bsuir.graphics.model.Texture;
import org.bsuir.graphics.model.Vertex;
import org.bsuir.graphics.service.VectorService;
import org.bsuir.graphics.utils.ProjectConstants;

import java.awt.*;
import java.awt.image.BufferedImage;

public class PhongLight {

    private final Vertex theSunVector;
    private final VectorService vectorService = new VectorService();
    private final CoordsMapper mapper;

    private static final float ambientStrength = 0.1f;
    private static final float diffuseStrength = 1f;
    private static final float specularStrength = 1f;
    private static final float specularShines = 42;

    public PhongLight(CoordsMapper mapper) {

        this.mapper = mapper;
        this.theSunVector = vectorService.normalize(ProjectConstants.THE_SUN);
    }

    public Color calculatePixelColor(ModelObject object, Vertex normal, Vertex pixelPosition, Texture imageTextile) {

        double[] ambientColorValue = ambientLighting(object, imageTextile);
        double[] diffuseColorValue = diffuseLighting(normal, object, imageTextile);
        double[] specularColorValue = specularLighting(normal, pixelPosition, object, imageTextile);

        try {
            var returnColor = new Color(
                Math.max(Math.min((int) (ambientColorValue[0] + diffuseColorValue[0] + specularColorValue[0]), 255), 0),
                Math.max(Math.min((int) (ambientColorValue[1] + diffuseColorValue[1] + specularColorValue[1]), 255), 0),
                Math.max(Math.min((int) (ambientColorValue[2] + diffuseColorValue[2] + specularColorValue[2]), 255), 0)
            );
            return returnColor;
        } catch (IllegalArgumentException ex) {
            throw ex;
        }
    }

    private double[] ambientLighting(ModelObject object, Texture imageTextile) {

        double[] color = {255, 255, 255};

        float[] aColor = object.getMaterial().getKa();
        if (aColor != null) {
            color[0] = aColor[0] * 255;
            color[1] = aColor[1] * 255;
            color[2] = aColor[2] * 255;
        }

        for (int i = 0; i < 3; i++) {
            color[i] *= ambientStrength;
        }

        return color;
    }

    private double[] diffuseLighting(Vertex pixelVector, ModelObject object, Texture imageTextile) {

        double[] color = {255, 255, 255};

        float[] dColor = object.getMaterial().getKd();
        if (dColor != null) {
            color[0] = dColor[0] * 255;
            color[1] = dColor[1] * 255;
            color[2] = dColor[2] * 255;
        }

        double scalar = Math.max(vectorService.scalarMultiply(vectorService.normalize(pixelVector), theSunVector), 0);

        if (object.getMaterial().getMap_Kd() != null) {
            int diffuseColor = getBufferedImageTextile(object.getMaterial().getMap_Kd(), imageTextile);
            color[0] *= scalar * ((diffuseColor & 0x00ff0000) >> 16) / 255.0 * diffuseStrength;
            color[1] *= scalar * ((diffuseColor & 0x0000ff00) >> 8) / 255.0 * diffuseStrength;
            color[2] *= scalar * (diffuseColor & 0x000000ff) / 255.0 * diffuseStrength;
        } else {
            for (int i = 0; i < 3; i++) {
                color[i] = color[i] * scalar * diffuseStrength;
            }
        }
        return color;
    }

    private double[] specularLighting(
        Vertex pixelVector,
        Vertex pixelPosition,
        ModelObject object,
        Texture imageTextile
    ) {

        double[] color = {255, 255, 255};

        float[] sColor = object.getMaterial().getKs();
        if (sColor != null) {
            color[0] = sColor[0] * 255;
            color[1] = sColor[1] * 255;
            color[2] = sColor[2] * 255;
        }

        Vertex eye = vectorService.normalize(vectorService.subtract(ProjectConstants.EYE, pixelPosition));

        Vertex specularDirection = vectorService.normalize(vectorService.add(theSunVector, eye));
        double specular = vectorService.scalarMultiply(vectorService.normalize(pixelVector), specularDirection);
        double scalar;
        if (object.getMaterial().getNs() != 0) {
            scalar = Math.pow(Math.max(specular, 0), object.getMaterial().getNs());
        } else {
            scalar = Math.pow(Math.max(specular, 0), specularShines);
        }

        if (object.getMaterial().getMap_Ks() != null) {
            int specularColor = getBufferedImageTextile(object.getMaterial().getMap_Kd(), imageTextile);
            color[0] *= scalar * ((specularColor & 0x00ff0000) >> 16) / 255.0 * specularStrength;
            color[1] *= scalar * ((specularColor & 0x0000ff00) >> 8) / 255.0 * specularStrength;
            color[2] *= scalar * (specularColor & 0x000000ff) / 255.0 * specularStrength;
        } else {
            for (int i = 0; i < 3; i++) {
                color[i] = color[i] * scalar * specularStrength;
            }
        }
        return color;
    }

    private int getBufferedImageTextile(BufferedImage image, Texture imageTextile) {

        int xCord = Math.round((imageTextile.u * image.getWidth()));
        int yCord = Math.round(((1 - imageTextile.v) * image.getHeight()));
        if (xCord >= image.getWidth()) {
            xCord -= image.getWidth();
        } else if (xCord < 0) {
            xCord += image.getWidth();
        }
        if (yCord >= image.getHeight()) {
            yCord -= image.getHeight();
        } else if (yCord < 0) {
            yCord += image.getHeight();
        }
        return image.getRGB(xCord, yCord);
    }
}
