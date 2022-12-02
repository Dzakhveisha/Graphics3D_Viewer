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

        if (object.getDiffuseMap() != null) {
            int ambientColor = getBufferedImageTextile(object.getDiffuseMap(), imageTextile);
            color[0] *= ((ambientColor & 0x00ff0000) >> 16) / 255.0 * ambientStrength;
            color[1] *= ((ambientColor & 0x0000ff00) >> 8) / 255.0 * ambientStrength;
            color[2] *= (ambientColor & 0x000000ff) / 255.0 * ambientStrength;
        } else {
            for (int i = 0; i < 3; i++) {
                color[i] *= ambientStrength;
            }
        }
        return color;
    }

    private double[] diffuseLighting(Vertex pixelVector, ModelObject object, Texture imageTextile) {

        double[] color = {255, 255, 255};

        double scalar;
        if (object.getNormalMap() != null) {
            int normal = getBufferedImageTextile(object.getNormalMap(), imageTextile);
            Vertex mapNormal = new Vertex(
                (float) (((normal & 0x00ff0000) >> 16) / 255.0 * 2 - 1),
                (float) (((normal & 0x0000ff00) >> 8) / 255.0 * 2 - 1),
                (float) (((normal & 0x000000ff) / 255.0 * 2 - 1))
            );
            scalar =
                Math.max(vectorService.scalarMultiply(vectorService.normalize(mapper.fromModelSpaceToWorldSpace(mapNormal)), theSunVector), 0);
        } else {
            scalar = Math.max(vectorService.scalarMultiply(vectorService.normalize(pixelVector), theSunVector), 0);
        }
        if (object.getDiffuseMap() != null) {
            int diffuseColor = getBufferedImageTextile(object.getDiffuseMap(), imageTextile);
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
        Vertex eye;
        if (object.getNormalMap() != null) {
            int normal = getBufferedImageTextile(object.getNormalMap(), imageTextile);
            Vertex mapNormal = new Vertex(
                (float) (((normal & 0x00ff0000) >> 16) / 255.0 * 2 - 1),
                (float) (((normal & 0x0000ff00) >> 8) / 255.0 * 2 - 1),
                (float) (((normal & 0x000000ff) / 255.0 * 2 - 1))
            );
            eye = vectorService.normalize(vectorService.subtract(ProjectConstants.EYE,
                mapper.fromModelSpaceToWorldSpace(mapNormal)));
        } else {
            eye = vectorService.normalize(vectorService.subtract(ProjectConstants.EYE, pixelPosition));
        }

        Vertex specularDirection = vectorService.normalize(vectorService.add(theSunVector, eye));
        double specular = vectorService.scalarMultiply(vectorService.normalize(pixelVector), specularDirection);
        double scalar = Math.pow(Math.max(specular, 0), specularShines);

        if (object.getReflectMap() != null) {
            int specularColor = getBufferedImageTextile(object.getReflectMap(), imageTextile);
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
