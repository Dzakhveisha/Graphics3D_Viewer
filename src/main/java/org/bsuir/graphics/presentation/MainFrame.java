package org.bsuir.graphics.presentation;

import org.bsuir.graphics.light.LambLight;
import org.bsuir.graphics.mapper.CoordsMapper;
import org.bsuir.graphics.model.DataReference;
import org.bsuir.graphics.model.Face;
import org.bsuir.graphics.model.Model;
import org.bsuir.graphics.model.Vertex;
import org.bsuir.graphics.scaner.ObjScanner;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MainFrame implements Runnable {

    private static final int SCREEN_WIDTH = 1280;
    private static final int SCREEN_HEIGHT = 720;
    double lastX = SCREEN_WIDTH / 2;
    double lastY = SCREEN_HEIGHT / 2;

    private Model model;

    private final List<Vertex> vertices;

    private final CoordsMapper mapper = new CoordsMapper();
    private static final DrawUtils drawer = new DrawUtils();

    private final LambLight lightness;
    private final JFrame frame;

    public MainFrame() {

        ClassLoader classLoader = getClass().getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream("Ford_Mustang_Shelby_GT500KR.obj"); //Ford_Mustang_Shelby_GT500KR.obj

        final Reader reader = new InputStreamReader(inputStream);

        ObjScanner scanRunner = new ObjScanner();
        try {
            scanRunner.run(new BufferedReader(reader));
        } catch (IOException ex) {

        }

        model = scanRunner.getParser().getModel();

        lightness = new LambLight(model.getNormals());

        vertices = new ArrayList<>();
        for (Vertex v : model.getVertices()) {
            v = mapper.fromModelSpaceToViewPort(v);
            vertices.add(v);
        }

        frame = new JFrame() {

            @Override
            public void paint(Graphics g) {
                Image img = createBufferedImage();
                g.drawImage(img, 0, 0, this);
            }
        };

        frame.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {

                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    mapper.move(vertices, 0, -10);
                } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    mapper.move(vertices, 0, 10);
                } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    mapper.move(vertices, -10, 0);
                } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    mapper.move(vertices, 10, 0);
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });

        frame.addMouseWheelListener(e -> {

            mapper.scale(e.getPreciseWheelRotation());
            int i = 0;

            for (Vertex vertex : model.getVertices()) {
                vertex = mapper.fromModelSpaceToViewPort(vertex);
                vertices.set(i, vertex);
                i++;
            }
        });

        frame.addMouseMotionListener(new MouseMotionListener() {

            @Override
            public void mouseDragged(MouseEvent e) {

                double xpos = e.getX();
                double ypos = e.getY();

                double xoffset = xpos - lastX;
                double yoffset = lastY - ypos;
                lastX = xpos;
                lastY = ypos;

                double sensitivity = 0.01;
                xoffset *= sensitivity;
                yoffset *= sensitivity;

                mapper.rotate(yoffset, xoffset);

                int i = 0;

                for (Vertex vertex : model.getVertices()) {
                    vertex = mapper.fromModelSpaceToViewPort(vertex);
                    vertices.set(i, vertex);
                    i++;
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {

                lastX = e.getX();
                lastY = e.getY();
            }
        });

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    @Override
    public void run() {

        while (running) {
            updateFrame();
        }
    }

    private BufferedImage createBufferedImage() {

        BufferedImage bufferedImage = new BufferedImage(SCREEN_WIDTH, SCREEN_HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics g = bufferedImage.getGraphics();

        draw3D(g);

        return bufferedImage;
    }

    private void draw3D(Graphics graphics) {
        drawer.initBuffer();
        model.getObjects().parallelStream().forEach(object -> {
            for (Face face : object.getFaces()) {
                Color faceColor = lightness.calcLightness(face.getReferences());
                for (int i = 0; i < face.getReferences().size(); i++) {
                    if (i + 1 == face.getReferences().size()) {
                        drawer.drawLine(graphics, faceColor,
                            (int) vertices.get(face.getReferences().get(i).vertexIndex - 1).x,
                            (int) vertices.get(face.getReferences().get(i).vertexIndex - 1).y,
                            (int) vertices.get(face.getReferences().get(0).vertexIndex - 1).x,
                            (int) vertices.get(face.getReferences().get(0).vertexIndex - 1).y);
                    } else {
                        drawer.drawLine(graphics, faceColor,
                            (int) vertices.get(face.getReferences().get(i).vertexIndex - 1).x,
                            (int) vertices.get(face.getReferences().get(i).vertexIndex - 1).y,
                            (int) vertices.get(face.getReferences().get(i + 1).vertexIndex - 1).x,
                            (int) vertices.get(face.getReferences().get(i + 1).vertexIndex - 1).y);
                    }
                }
                face_rasterization(graphics, faceColor, face);
            }
        });
    }

    private void face_rasterization(Graphics graphics, Color faceColor, Face face) {
        List<Vertex> list = face.getReferences()
                .stream()
                .map(dataReference -> vertices.get(dataReference.vertexIndex - 1))
                .collect(Collectors.toList());
        drawer.face_rasterization(graphics, faceColor, list);

    }

    private void updateFrame() {
        frame.repaint();
    }

    private boolean running;

    public synchronized void start() {
        new Thread(this).start();
        running = true;
    }

    public synchronized void stop() {
        running = false;
    }
}
