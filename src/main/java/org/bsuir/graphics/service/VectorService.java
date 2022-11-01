package org.bsuir.graphics.service;

import org.bsuir.graphics.model.Vertex;

public class VectorService {

    public double scalarMultiply(Vertex vertex1, Vertex vertex2) {

        return vertex1.x * vertex2.x + vertex1.y * vertex2.y + vertex1.z * vertex2.z;
    }

    public Vertex subtract(Vertex vertex1, Vertex vertex2) {

        return new Vertex(
            vertex1.x - vertex2.x,
            vertex1.y - vertex2.y,
            vertex1.z - vertex2.z
        );
    }

    public Vertex add(Vertex vertex1, Vertex vertex2) {

        return new Vertex(
            vertex1.x + vertex2.x,
            vertex1.y + vertex2.y,
            vertex1.z + vertex2.z
        );
    }

    public Vertex vectorMultiply(Vertex vertex1, Vertex vertex2) {

        return new Vertex(
            vertex1.y * vertex2.z - vertex2.y - vertex1.z,
            vertex1.x * vertex2.z - vertex2.x * vertex1.z,
            vertex1.x * vertex2.y - vertex2.x * vertex1.y
        );
    }

    public Vertex multiply(float coefficient, Vertex vertex) {

        return new Vertex(
            coefficient * vertex.x,
            coefficient * vertex.y,
            coefficient * vertex.z
        );
    }

    public double getLength(Vertex vertex) {

        return Math.sqrt(vertex.x * vertex.x + vertex.y * vertex.y + vertex.z * vertex.z);
    }

    public Vertex normalize(Vertex vertex) {

        double length = getLength(vertex);
        return new Vertex(
            (float) (vertex.x / length),
            (float) (vertex.y / length),
            (float) (vertex.z / length)
        );
    }
}
