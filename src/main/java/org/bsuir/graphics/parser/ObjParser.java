package org.bsuir.graphics.parser;

import org.bsuir.graphics.model.*;

public class ObjParser {

    private final Model model = new Model();
    private ModelObject currentObject;
    private Face currentFace;

    public Model getModel() {

        return model;
    }

    public void onVertex(float x, float y, float z, float w) {

        final Vertex vertex = new Vertex(x, y, z);
        model.getVertices().add(vertex);
    }

    public void onObject(String objectName) {

        currentObject = new ModelObject();
        //currentObject.setMaterialsName("shovel_Normal.png", "shovel_diffuse.png", "shovel_Reflect.png");
        model.getObjects().add(currentObject);
    }

    public void onNormal(float x, float y, float z) {

        final Vertex normal = new Vertex(x, y, z);
        model.getNormals().add(normal);
    }

    public void onFaceBegin() {

        assureCurrentObject();
        currentFace = new Face();
        currentObject.getFaces().add(currentFace);
    }

    public void onDataReference(int vertexIndex, int texCoordIndex, int normalIndex) {

        final DataReference reference = new DataReference();

        reference.vertexIndex = vertexIndex;
        reference.texCoordIndex = texCoordIndex;
        reference.normalIndex = normalIndex;
        currentFace.getReferences().add(reference);
    }

    public void onMaterialReference(Material material){
        assureCurrentObject();
        currentObject.setMaterial(material);
    }

    private void assureCurrentObject() {

        if (currentObject != null) {
            return;
        }
        currentObject = new ModelObject("Default");
        model.getObjects().add(currentObject);
    }

    public void onTextureCoordinate(float u, float v, float w) {

        u = u % 1;
        if (u < 0) {
            u += 1;
        }

        v = v % 1;
        if (v < 0) {
            v += 1;
        }
        Texture texCoord = new Texture(u, v, w);

        model.getTextures().add(texCoord);
    }
}
