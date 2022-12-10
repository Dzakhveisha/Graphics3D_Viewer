package org.bsuir.graphics.model;

import java.util.ArrayList;
import java.util.List;

public class ModelObject {

    private final List<Face> faces = new ArrayList<>();
    private String modelName;
    private Material material;

    public List<Face> getFaces() {
        return faces;
    }

    public ModelObject() {

    }

    public ModelObject(String modelName) {

        this.modelName = modelName;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }
}
