package org.bsuir.graphics.scaner;

public class ScanDataReference {

    private int vertexIndex = 0;
    private int texCoordIndex = 0;
    private int normalIndex = 0;
    private boolean hasTexCoordIndex = false;
    private boolean hasNormalIndex = false;

    public ScanDataReference() {

    }

    public void parse(String segment) {

        final String[] references = segment.split("/");
        vertexIndex = parseInt(references[0]);
        hasTexCoordIndex = (references.length >= 2) && !references[1].isEmpty();
        if (hasTexCoordIndex) {
            texCoordIndex = parseInt(references[1]);
        }
        hasNormalIndex = (references.length >= 3) && !references[2].isEmpty();
        if (hasNormalIndex) {
            normalIndex = parseInt(references[2]);
        }
    }

    public int getVertexIndex() {

        return vertexIndex;
    }

    public int getTexCoordIndex() {

        return hasTexCoordIndex ? texCoordIndex : 0;
    }

    public int getNormalIndex() {

        return hasNormalIndex ? normalIndex : 0;
    }

    private static int parseInt(String text) {

        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException ex) {
            return 0;
        }
    }
}
