package org.bsuir.graphics.model;

import java.util.ArrayList;
import java.util.List;

public class ModelObject {

    private final List<Face> faces = new ArrayList<>();
    private String name;

    public ModelObject() {

    }

    public ModelObject(String name) {

        this.name = name;
    }

    public List<Face> getFaces() {
        return faces;
    }
}
