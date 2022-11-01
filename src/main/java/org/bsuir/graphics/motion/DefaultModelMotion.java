package org.bsuir.graphics.motion;

import org.bsuir.graphics.mapper.CoordsMapper;
import org.bsuir.graphics.model.Model;
import org.bsuir.graphics.model.Vertex;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.List;

public class DefaultModelMotion {

    private Frame frame;

    private static final float MOUSE_SENSITIVITY = 0.01f;
    private double lastMouseXPlacement = 0;
    private double lastMouseYPlacement = 0;

    public DefaultModelMotion(Frame frame) {

        this.frame = frame;
    }

    public Frame addMouseMotionToFrame(CoordsMapper mapper, Model model, List<Vertex> vertices, List<Vertex> normals) {

        frame.addMouseMotionListener(new MouseMotionListener() {

            @Override
            public void mouseDragged(MouseEvent e) {

                double currentXPosition = e.getX();
                double currentYPosition = e.getY();

                double xoffset = currentXPosition - lastMouseXPlacement;
                double yoffset = lastMouseYPlacement - currentYPosition;
                lastMouseXPlacement = currentXPosition;
                lastMouseYPlacement = currentYPosition;

                xoffset *= MOUSE_SENSITIVITY;
                yoffset *= MOUSE_SENSITIVITY;

                mapper.rotate(yoffset, xoffset);

                int i = 0;

                for (Vertex vertex : model.getVertices()) {
                    vertex = mapper.fromModelSpaceToViewPort(vertex);
                    vertices.set(i, vertex);
                    i++;
                }

                i = 0;

                for (Vertex vertex : model.getNormals()) {
                    vertex = mapper.fromModelSpaceToWorldSpace(vertex);
                    normals.set(i, vertex);
                    i++;
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {

                lastMouseXPlacement = e.getX();
                lastMouseYPlacement = e.getY();
            }
        });

        return frame;
    }

    public Frame addMouseWheelMotionToFrame(CoordsMapper mapper, Model model, List<Vertex> vertices) {

        frame.addMouseWheelListener(e -> {

            mapper.scale(e.getPreciseWheelRotation());
            int i = 0;

            for (Vertex vertex : model.getVertices()) {
                vertex = mapper.fromModelSpaceToViewPort(vertex);
                vertices.set(i, vertex);
                i++;
            }
        });

        return frame;
    }

    public Frame addKeyMotionToFrame(CoordsMapper mapper, Model model, List<Vertex> vertices, List<Vertex> normals) {

        frame.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {

                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    mapper.move(0, -1);

                    int i = 0;

                    for (Vertex vertex : model.getVertices()) {
                        vertex = mapper.fromModelSpaceToViewPort(vertex);
                        vertices.set(i, vertex);
                        i++;
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    mapper.move(0, 1);

                    int i = 0;

                    for (Vertex vertex : model.getVertices()) {
                        vertex = mapper.fromModelSpaceToViewPort(vertex);
                        vertices.set(i, vertex);
                        i++;
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    mapper.move(-1, 0);

                    int i = 0;

                    for (Vertex vertex : model.getVertices()) {
                        vertex = mapper.fromModelSpaceToViewPort(vertex);
                        vertices.set(i, vertex);
                        i++;
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    mapper.move(1, 0);
                    int i = 0;

                    for (Vertex vertex : model.getVertices()) {
                        vertex = mapper.fromModelSpaceToViewPort(vertex);
                        vertices.set(i, vertex);
                        i++;
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });

        return frame;
    }
}
