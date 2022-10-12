package org.bsuir.graphics.model;

import java.util.ArrayList;
import java.util.List;

public class Face {

    private final List<DataReference> references = new ArrayList<>(4);

    public Face() {

    }

    public List<DataReference> getReferences() {
        return references;
    }
}
