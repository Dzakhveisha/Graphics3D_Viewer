package org.bsuir.graphics.scaner;

import org.bsuir.graphics.model.Material;
import org.bsuir.graphics.parser.ObjParser;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ObjScanner {

    private static final String COMMAND_VERTEX = "v";
    private static final String COMMAND_OBJECT = "o";
    private static final String COMMAND_FACE = "f";
    private static final String COMMAND_NORMAL = "vn";
    private static final String COMMAND_TEXTURE = "vt";
    private static final String COMMAND_MATERIAL_REF = "usemtl";
    private static final String COMMAND_MATERIAL_LIB = "mtllib";

    private final ObjParser handler = new ObjParser();
    private final LineScanner command = new LineScanner();
    private final ScanDataReference dataReference = new ScanDataReference();
    private final MtlScaner mtlScaner = new MtlScaner();
    private List<Material> materialList = new ArrayList<>();

    public ObjParser getParser() {

        return handler;
    }

    public void run(BufferedReader reader) throws IOException {

        while (command.parse(reader)) {
            if (command.isEmpty()) {
                continue;
            } else if (command.isComment()) {
                continue;
            } else if (command.isCommand(COMMAND_VERTEX)) {
                processVertex(command);
            } else if (command.isCommand(COMMAND_OBJECT)) {
                processObject(command);
            } else if (command.isCommand(COMMAND_NORMAL)) {
                processNormal(command);
            } else if (command.isCommand(COMMAND_FACE)) {
                processFace(command);
            } else if (command.isCommand(COMMAND_TEXTURE)) {
                processTexture(command);
            } else if (command.isCommand(COMMAND_MATERIAL_LIB)) {
                processMaterialLib(command);
            } else if (command.isCommand(COMMAND_MATERIAL_REF)) {
                processMaterialReference(command);
            }
        }
    }

    private void processMaterialLib(LineScanner command) {

        ClassLoader classLoader = getClass().getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(command.getStringParam(0));

        if (inputStream == null) {
            throw new RuntimeException("Could not find file in the resources");
        }
        final Reader reader = new InputStreamReader(inputStream);

        try {
            mtlScaner.run(new BufferedReader(reader));
        } catch (IOException ex) {
            System.out.println("Scanner exception. Exception:" + ex.getMessage() + " " + ex.getCause());
        }

        this.materialList = mtlScaner.getParser().getMaterials();
    }

    private void processTexture(LineScanner command) {

        float u = command.getParam(0);
        float v = command.getParam(1);
        float w = command.getParam(2);
        handler.onTextureCoordinate(u, v, w);
    }

    private void processVertex(LineScanner command) {

       final float x = command.getParam(0) / 50;
        final float y = command.getParam(1) / 50;
        final float z = command.getParam(2) / 50;
        final float w = command.getParam(3) / 50;
     /*   final float x = command.getParam(0);
        final float y = command.getParam(1);
        final float z = command.getParam(2);
        final float w = command.getParam(3);*/
        handler.onVertex(x, y, z, w);
    }

    private void processObject(LineScanner command) {

        final String name = command.getStringParam(0).trim();
        handler.onObject(name);
    }

    private void processFace(LineScanner command) {

        handler.onFaceBegin();
        for (int i = 0; i < command.getParameterCount(); ++i) {
            dataReference.parse(command.getStringParam(i));
            final int vertexIndex = dataReference.getVertexIndex();
            final int texCoordIndex = dataReference.getTexCoordIndex();
            final int normalIndex = dataReference.getNormalIndex();
            handler.onDataReference(vertexIndex, texCoordIndex, normalIndex);
        }
    }

    private void processNormal(LineScanner command) {

        final float x = command.getParam(0);
        final float y = command.getParam(1);
        final float z = command.getParam(2);
        handler.onNormal(x, y, z);
    }

    private void processMaterialReference(LineScanner command) {
        Material material = materialList.stream()
                .filter(m -> m.getName().equals(command.getStringParam(0)))
                .findFirst()
                .orElseThrow(RuntimeException::new);
        handler.onMaterialReference(material);
    }
}
