package org.bsuir.graphics.scaner;

import java.io.BufferedReader;
import java.io.IOException;

public class LineScanner {

    private static final String WHITE_SPACE_PATTERN = "[\\s]+";
    private static final String COMMENT_SEPARATOR = "#";
    private static final String LINE_EXTENSION = "\\";

    private final StringBuilder logicalLineBuilder = new StringBuilder();
    private String logicalLine;
    private String[] segments;

    public boolean parse(BufferedReader reader) throws IOException {
        final String line = readLogicalLine(reader);
        if (line == null) {
            return false;
        }
        logicalLine = line.trim();
        segments = logicalLine.split(WHITE_SPACE_PATTERN);
        return true;
    }

    private String readLogicalLine(BufferedReader reader) throws IOException {

        String line = reader.readLine();
        if (line == null) {
            return null;
        }
        if (!line.endsWith(LINE_EXTENSION)) {
            return line;
        }

        logicalLineBuilder.setLength(0);
        while ((line != null) && (line.endsWith(LINE_EXTENSION))) {
            final String lineContent = line.substring(0, line.length() - 1);
            logicalLineBuilder.append(lineContent);
            line = reader.readLine();
        }
        if (line != null) {
            logicalLineBuilder.append(line);
        }
        return logicalLineBuilder.toString();
    }

    public boolean isEmpty() {

        return (segments[0].isEmpty());
    }

    public boolean isComment() {

        return segments[0].startsWith(COMMENT_SEPARATOR);
    }

    public boolean isCommand(String commandName) {

        return commandName.equals(segments[0]);
    }

    public int getParameterCount() {
        return Math.max(0, segments.length - 1);
    }

    public String getStringParam(int index) {
        return segments[index + 1];
    }

    public float getParam(int index){

        final int segmentIndex = (index + 1);
        if (segmentIndex >= segments.length) {
            return 1;
        }
        return getFloatParam(index);
    }

    private float getFloatParam(int index){
        try {
            return Float.parseFloat(getStringParam(index));
        } catch (NumberFormatException ex) {
            return 0;
        }
    }
}
