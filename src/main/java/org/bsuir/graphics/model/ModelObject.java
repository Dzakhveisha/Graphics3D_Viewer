package org.bsuir.graphics.model;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ModelObject {

    private final List<Face> faces = new ArrayList<>();
    private String modelName;
    private BufferedImage normalMap;
    private BufferedImage reflectMap;
    private BufferedImage diffuseMap;

    public ModelObject() {

    }

    public ModelObject(String modelName) {

        this.modelName = modelName;
    }

    public void setMaterialsName(String normalMapName, String diffuseMapName, String reflectMapName) {

        if (normalMapName != null) {
            ClassLoader classLoader = getClass().getClassLoader();
            try(InputStream inputStream = classLoader.getResourceAsStream("maps/" + normalMapName);) {
                if (inputStream != null) {
                    this.normalMap = ImageIO.read(inputStream);
                } else {
                    System.out.println("file with name " + normalMapName + " not found");
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        if (diffuseMapName != null) {
            ClassLoader classLoader = getClass().getClassLoader();
            try(InputStream inputStream = classLoader.getResourceAsStream("maps/" + diffuseMapName);) {
                if (inputStream != null) {
                    this.diffuseMap = ImageIO.read(inputStream);
                } else {
                    System.out.println("file with name " + diffuseMapName + " not found");
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        if (reflectMapName != null) {
            ClassLoader classLoader = getClass().getClassLoader();
            try(InputStream inputStream = classLoader.getResourceAsStream("maps/" + reflectMapName);) {
                if (inputStream != null) {
                    this.reflectMap = ImageIO.read(inputStream);
                } else {
                    System.out.println("file with name " + reflectMapName + " not found");
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public BufferedImage getDiffuseMap() {

        return diffuseMap;
    }

    public BufferedImage getNormalMap() {

        return normalMap;
    }

    public BufferedImage getReflectMap() {

        return reflectMap;
    }

    public List<Face> getFaces() {
        return faces;
    }
}
