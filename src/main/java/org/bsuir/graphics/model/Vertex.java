package org.bsuir.graphics.model;

import static java.lang.Float.floatToRawIntBits;

public class Vertex {

    public float x = 0.0f;
    public float y = 0.0f;
    public float z = 0.0f;
    public float w = 1.0f;

    public float wAd = 1.0f;

    public Vertex() {

    }

    public Vertex(float x, float y, float z) {

        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vertex(float x, float y, float z, float w) {

        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public Vertex(float x, float y, float z, float w, float w1) {

        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
        this.wAd = wAd;
    }

    public void set(float x, float y, float z, float w) {

        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public void set(Vertex vertex) {

        this.x = vertex.x;
        this.y = vertex.y;
        this.z = vertex.z;
        this.w = vertex.w;
    }

    public float getElement(int i) {

        if (i == 0) {
            return x;
        } else if (i == 1) {
            return y;
        } else if (i == 2) {
            return z;
        } else if (i == 3) {
            return w;
        } else {
            return 0;
        }
    }

    public void setElement(int i, float value) {

        if (i == 0) {
            x = value;
        } else if (i == 1) {
            y = value;
        } else if (i == 2) {
            z = value;
        } else if (i == 3) {
            w = value;
        }
    }

    @Override
    public boolean equals(java.lang.Object obj) {

        if (obj == this) {
            return true;
        }
        if (!(obj.getClass().equals(Vertex.class))) {
            return false;
        }
        Vertex vertexObj = ((Vertex) obj);
        return (floatToRawIntBits(x) == floatToRawIntBits(vertexObj.x))
            && (floatToRawIntBits(y) == floatToRawIntBits(vertexObj.y))
            && (floatToRawIntBits(z) == floatToRawIntBits(vertexObj.z))
            && (floatToRawIntBits(w) == floatToRawIntBits(vertexObj.w));
    }

    @Override
    public int hashCode() {

        int result = floatToRawIntBits(x);
        result = result * 31 + floatToRawIntBits(y);
        result = result * 31 + floatToRawIntBits(z);
        result = result * 31 + floatToRawIntBits(w);
        return result;
    }
}
