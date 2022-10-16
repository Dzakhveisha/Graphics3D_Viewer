package org.bsuir.graphics.model;

public class DataReference {

    public static final int UNDEFINED_INDEX = -1;
    public int vertexIndex = UNDEFINED_INDEX;
    public int normalIndex = UNDEFINED_INDEX;
    public int texCoordIndex = UNDEFINED_INDEX;

    public DataReference() {

    }

    public int getNormalIndex() {

        return normalIndex;
    }

    @Override
    public boolean equals(java.lang.Object obj) {

        if (obj == this) {
            return true;
        }
        if (!(obj.getClass().equals(DataReference.class))) {
            return false;
        }
        DataReference dataReferenceObj = ((DataReference) obj);
        return (vertexIndex == dataReferenceObj.vertexIndex)
            && (texCoordIndex == dataReferenceObj.texCoordIndex)
            && (normalIndex == dataReferenceObj.normalIndex);
    }

    @Override
    public int hashCode() {

        int result = vertexIndex;
        result = result * 31 + texCoordIndex;
        result = result * 31 + normalIndex;
        return result;
    }
}
