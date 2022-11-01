package org.bsuir.graphics.presentation;

import org.bsuir.graphics.light.LambLight;
import org.bsuir.graphics.mapper.CoordsMapper;
import org.bsuir.graphics.model.DataReference;
import org.bsuir.graphics.model.Face;
import org.bsuir.graphics.model.Model;
import org.bsuir.graphics.model.Vertex;
import org.bsuir.graphics.motion.DefaultModelMotion;
import org.bsuir.graphics.scaner.ObjScanner;
import org.bsuir.graphics.service.VectorService;
import org.bsuir.graphics.utils.ProjectConstants;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;

public class MainFrame implements Runnable {

    private static final int SCREEN_WIDTH = ProjectConstants.SCREEN_WIDTH;
    private static final int SCREEN_HEIGHT = ProjectConstants.SCREEN_HEIGHT;
    private final Model model;

    private LambLight lambLight;
    private final List<Vertex> vertices;
    private final List<Vertex> normals;
    private final CoordsMapper mapper = new CoordsMapper();
    private static final DrawUtils drawer = new DrawUtils();

    private static final Drawer optDrawer = new Drawer();
    private VectorService vectorService = new VectorService();

    private final Vertex eye = vectorService.normalize(ProjectConstants.EYE);
    private final JFrame frame;

    public MainFrame() {

        model = readFile().getParser().getModel();

        vertices = new ArrayList<>();
        model.getVertices().forEach(vertex -> {
            vertices.add(mapper.fromModelSpaceToViewPort(vertex));
        });

        normals = new ArrayList<>();
        normals.addAll(model.getNormals());

        lambLight = new LambLight(normals);

        frame = new JFrame() {

            @Override
            public void paint(Graphics g) {

                Image img = createBufferedImage();
                g.drawImage(img, 0, 0, this);
            }
        };

        DefaultModelMotion motion = new DefaultModelMotion(frame);
        motion.addMouseMotionToFrame(mapper, model, vertices, normals);
        motion.addMouseWheelMotionToFrame(mapper, model, vertices);
        motion.addKeyMotionToFrame(mapper, model, vertices, normals);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private ObjScanner readFile() {

        ClassLoader classLoader = getClass().getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream("cat.obj");
        //Ford_Mustang_Shelby_GT500KR.obj

        if (inputStream == null) {
            throw new RuntimeException("Could not find file in the resources");
        }
        final Reader reader = new InputStreamReader(inputStream);

        ObjScanner scanRunner = new ObjScanner();
        try {
            scanRunner.run(new BufferedReader(reader));
        } catch (IOException ex) {
            System.out.println("Scanner exception. Exception:" + ex.getMessage() + " " + ex.getCause());
        }

        return scanRunner;
    }

    private BufferedImage createBufferedImage() {

        BufferedImage bufferedImage = new BufferedImage(SCREEN_WIDTH, SCREEN_HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics g = bufferedImage.getGraphics();

        draw3D(g);

        return bufferedImage;
    }

    private void draw3D(Graphics graphics) {

        drawer.initBuffer();
        optDrawer.initBuffer();
        model.getObjects().forEach(object -> {
            for (Face face : object.getFaces()) {
                if (normalsBraking(face.getReferences())) {

                    for (int i = 0; i < face.getReferences().size(); i++) {
                        if (i + 1 == face.getReferences().size()) {
                            drawer.drawLine(graphics,
                                normals.get(face.getReferences().get(i).normalIndex - 1),
                                normals.get(face.getReferences().get(0).normalIndex - 1),
                                Math.round(vertices.get(face.getReferences().get(i).vertexIndex - 1).x),
                                Math.round(vertices.get(face.getReferences().get(i).vertexIndex - 1).y),
                                Math.round(vertices.get(face.getReferences().get(0).vertexIndex - 1).x),
                                Math.round(vertices.get(face.getReferences().get(0).vertexIndex - 1).y),
                                Math.round(vertices.get(face.getReferences().get(i).vertexIndex - 1).z),
                                Math.round(vertices.get(face.getReferences().get(0).vertexIndex - 1).z));
                        } else {
                            drawer.drawLine(graphics,
                                normals.get(face.getReferences().get(i).normalIndex - 1),
                                normals.get(face.getReferences().get(i + 1).normalIndex - 1),
                                Math.round(vertices.get(face.getReferences().get(i).vertexIndex - 1).x),
                                Math.round(vertices.get(face.getReferences().get(i).vertexIndex - 1).y),
                                Math.round(vertices.get(face.getReferences().get(i + 1).vertexIndex - 1).x),
                                Math.round(vertices.get(face.getReferences().get(i + 1).vertexIndex - 1).y),
                                Math.round(vertices.get(face.getReferences().get(i).vertexIndex - 1).z),
                                Math.round(vertices.get(face.getReferences().get(i + 1).vertexIndex - 1).z));
                        }
                    }
                    faceRasterization(graphics, face);
                }
            }
        });
    }

    private void faceRasterization(Graphics graphics, Face face) {

        List<Vertex> list = face.getReferences()
            .stream()
            .map(dataReference -> vertices.get(dataReference.vertexIndex - 1))
            .collect(Collectors.toList());
        List<Vertex> normalList = face.getReferences()
            .stream()
            .map(dataReference -> normals.get(dataReference.normalIndex - 1)).toList();
        drawer.face_rasterization(graphics, list, normalList);

    }

    private boolean normalsBraking(List<DataReference> dataReferenceList) {

        Vertex vector1 = vertices.get(dataReferenceList.get(0).vertexIndex - 1);
        Vertex vector2 = vertices.get(dataReferenceList.get(1).vertexIndex - 1);
        Vertex vector3 = vertices.get(dataReferenceList.get(2).vertexIndex - 1);

        Vertex normal = vectorService.vectorMultiply(
            vectorService.subtract(vector1, vector2),
            vectorService.subtract(vector2, vector3)
        );

        return !(vectorService.scalarMultiply(vectorService.normalize(normal), eye) >= 0);
    }

    private boolean running;

    @Override
    public void run() {

        while (running) {
            updateFrame();
        }
    }

    public synchronized void start() {

        new Thread(this).start();
        running = true;
    }

    private void updateFrame() {

        frame.repaint();
    }
}
