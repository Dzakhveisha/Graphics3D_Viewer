package org.bsuir.graphics.presentation;

import org.bsuir.graphics.model.Face;
import org.bsuir.graphics.model.ModelObject;
import org.bsuir.graphics.model.Vertex;

import java.awt.*;
import java.util.List;
import java.util.concurrent.Callable;

public class DrawVertices implements Callable {

    private ModelObject object;
    private Graphics graphics;
    private DrawUtils drawer;
    private List<Vertex> vertices;

    public DrawVertices(ModelObject object, Graphics graphics, DrawUtils drawer, List<Vertex> vertices) {

        this.object = object;
        this.graphics = graphics;
        this.drawer = drawer;
        this.vertices = vertices;
    }


    @Override
    public Object call() throws Exception {



        return true;
    }
}
