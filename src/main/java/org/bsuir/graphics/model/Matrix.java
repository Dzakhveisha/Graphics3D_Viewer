package org.bsuir.graphics.model;

public class Matrix {

    private float[]  m_;

    public Matrix()
    {
        initialize();
        setIdentity();
    }

    private void initialize()
    {
        m_ = new float[16];
    }

    public void setIdentity()
    {
        for (int i=0; i<4; i++)
            for (int j=0; j<4; j++)
                m_[i*4 + j] = i == j ? 1.0f : 0.0f;
    }

    public double getElement (int i, int j)
    {
        return m_[i*4 + j];
    }

    public void setElement (int i, int j, float value)
    {
        m_[i*4 + j] = value;
    }

    public Vertex multiply (Vertex Vertex4)
    {
        Vertex product = new Vertex();

        for (int i = 0; i < 4; i++) {
            float value = 0.0f;
            for (int j = 0; j < 4; j++)
                value += getElement(i, j) * Vertex4.getElement (j);
            product.setElement (i, value);
        }

        return product;
    }

    public Matrix multiply (Matrix matrix)
    {
        Matrix product = new Matrix();

        for (int i = 0; i < 16; i += 4) {
            for (int j = 0; j < 4; j++) {
                product.m_[i + j] = 0.0f;
                for (int k = 0; k < 4; k++)
                    product.m_[i + j] += m_[i + k] * matrix.m_[k*4 + j];
            }
        }
        return product;
    }
}
