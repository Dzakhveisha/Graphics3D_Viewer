package org.bsuir.graphics.model;

import java.util.ArrayList;
import java.util.List;

public class Model {

    private final List<Vertex> vertices = new ArrayList<>();
    private final List<Normal> normals = new ArrayList<>();
    private final List<ModelObject> objects = new ArrayList<>();


    public List<Vertex> getVertices() {
        return vertices;
    }

    public List<Normal> getNormals() {
        return normals;
    }

    public List<ModelObject> getObjects() {
        return objects;
    }
}
