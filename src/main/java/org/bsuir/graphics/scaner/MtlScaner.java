package org.bsuir.graphics.scaner;

import org.bsuir.graphics.parser.MtlParser;

import java.io.*;
import java.util.Arrays;

public class MtlScaner {
    private static final String COMMAND_NEW_MATERIAL_LIB = "newmtl";
    private static final String COMMAND_KAMBIENT = "Ka";
    private static final String COMMAND_KDIFFUSE = "Kd";
    private static final String COMMAND_KSPECULAR = "Ks";
    private static final String COMMAND_NSPECULAR = "Ns";
    private static final String COMMAND_MAP_DIFFUSE = "map_Kd";

    private static final String COMMAND_MAP_SPECULAR = "map_Ks";

    private final MtlParser handler = new MtlParser();
    private final LineScanner command = new LineScanner();

    public MtlParser getParser() {

        return handler;
    }

    public void run(BufferedReader reader) throws IOException {

        while (command.parse(reader)) {
            if (command.isEmpty()) {
                continue;
            } else if (command.isComment()) {
                continue;
            } else if (command.isCommand(COMMAND_NEW_MATERIAL_LIB)) {
                processNewMaterialLib(command);
            } else if (command.isCommand(COMMAND_KAMBIENT)) {
                processKAmbient(command);
            } else if (command.isCommand(COMMAND_KDIFFUSE)) {
                processKDiffuse(command);
            } else if (command.isCommand(COMMAND_KSPECULAR)) {
                processKSpecular(command);
            } else if (command.isCommand(COMMAND_NSPECULAR)) {
                processNSpecular(command);
            } else if (command.isCommand(COMMAND_MAP_DIFFUSE)) {
                processDiffuseMap(command);
            } else if (command.isCommand(COMMAND_MAP_SPECULAR)) {
                processSpecularMap(command);
            }
        }
    }

    private void processSpecularMap(LineScanner command) {
        String map = command.getStringParam(0);
        handler.onSpecularMap(map);
    }

    private void processDiffuseMap(LineScanner command) {
        String map = command.getStringParam(0);
        handler.onDiffuseMap(map);
    }

    private void processNSpecular(LineScanner command) {
        int value = (int) command.getParam(0);
        handler.onNSpecular(value);
    }

    private void processKSpecular(LineScanner command) {
        float param1 = command.getParam(0);
        float param2 = command.getParam(1);
        float param3 = command.getParam(2);
        handler.onKSpecular(new float[]{param1, param2, param3});
    }

    private void processKDiffuse(LineScanner command) {
        float param1 = command.getParam(0);
        float param2 = command.getParam(1);
        float param3 = command.getParam(2);
        handler.onKDiffuse(new float[]{param1, param2, param3});
    }

    private void processKAmbient(LineScanner command) {
        float param1 = command.getParam(0);
        float param2 = command.getParam(1);
        float param3 = command.getParam(2);
        handler.onKAmbient(new float[]{param1, param2, param3});
    }

    private void processNewMaterialLib(LineScanner command) {
        handler.onNewMtl(command.getStringParam(0));
    }


}
