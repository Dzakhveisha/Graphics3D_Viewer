package org.bsuir.graphics.scaner;

import org.bsuir.graphics.parser.ObjParser;

import javax.sound.sampled.Line;
import java.io.BufferedReader;
import java.io.IOException;

public class ObjScanner {

    private static final String COMMAND_VERTEX = "v";
    private static final String COMMAND_OBJECT = "o";
    private static final String COMMAND_FACE = "f";
    private static final String COMMAND_NORMAL = "vn";

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
            }else if (command.isCommand(COMMAND_FACE)) {
                processFace(command);
            }
        }
    }

    private void processVertex(LineScanner command) {

       final float x = command.getParam(0) / 50;
        final float y = command.getParam(1) / 50;
        final float z = command.getParam(2) / 50;
        final float w = command.getParam(3) / 50;
//        final float x = command.getParam(0);
//        final float y = command.getParam(1);
//        final float z = command.getParam(2);
//        final float w = command.getParam(3);
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
}
