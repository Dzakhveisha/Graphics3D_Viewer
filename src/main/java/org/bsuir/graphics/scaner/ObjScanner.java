package org.bsuir.graphics.scaner;

import org.bsuir.graphics.parser.ObjParser;

import java.io.BufferedReader;
import java.io.IOException;

public class ObjScanner {

    private static final String COMMAND_VERTEX = "v";
    private static final String COMMAND_OBJECT = "o";
    private static final String COMMAND_FACE = "f";
    private static final String COMMAND_NORMAL = "vn";
    private static final String COMMAND_TEXTURE = "vt";
    private static final String COMMAND_MATERIAL_REF = "usemtl";

    private final ObjParser handler = new ObjParser();
    private final LineScanner command = new LineScanner();
    private final ScanDataReference dataReference = new ScanDataReference();

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
            } else if (command.isCommand(COMMAND_MATERIAL_REF)) {
                processMaterialReference(command);
            }
        }
    }

    private void processTexture(LineScanner command) {

        float u = command.getParam(0);
        float v = command.getParam(1);
        float w = command.getParam(2);
        handler.onTextureCoordinate(u, v, w);
    }

    private void processVertex(LineScanner command) {

     /*  final float x = command.getParam(0) / 50;
        final float y = command.getParam(1) / 50;
        final float z = command.getParam(2) / 50;
        final float w = command.getParam(3) / 50;*/
        final float x = command.getParam(0);
        final float y = command.getParam(1);
        final float z = command.getParam(2);
        final float w = command.getParam(3);
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

        String normalMapName = null;
        String diffuseMapName = null;
        String reflectMapName = null;
        if (command.getParameterCount() > 0) {
            for (int i = 0; i < command.getParameterCount(); i++) {
                final String name = command.getStringParam(i).trim();
                if (name.contains("Normal")) {
                    normalMapName = name;
                }
                if (name.contains("Reflect")) {
                    reflectMapName = name;
                }
                if (name.contains("diffuse")) {
                    diffuseMapName = name;
                }
            }
            handler.onMaterialReference(reflectMapName, diffuseMapName, normalMapName);
        } else {
            handler.onMaterialReference(null, null, null);
        }
    }
}
