package org.bsuir.graphics.service;

import org.bsuir.graphics.model.Texture;
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

    public Texture subtract(Texture texture1, Texture texture2) {

        return new Texture(
            texture1.u - texture2.u,
            texture1.v - texture2.v,
            texture1.w - texture2.w
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
            coefficient * vertex.z,
            coefficient * vertex.w,
            coefficient * vertex.wAd
        );
    }

    public double getLength(Vertex vertex) {

        return Math.sqrt(vertex.x * vertex.x + vertex.y * vertex.y + vertex.z * vertex.z);
    }

    public double getLength(Texture texture) {

        return Math.sqrt(texture.u * texture.u + texture.v * texture.v + texture.w * texture.w);
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
