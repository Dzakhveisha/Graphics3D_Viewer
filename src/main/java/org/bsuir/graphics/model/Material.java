package org.bsuir.graphics.model;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class Material {
    private String name;
    private float[] Ka;
    private float[] Kd;
    private boolean d;
    private float Tr;
    private float illum;
    private float[] Ks;
    private int Ns;
    private BufferedImage map_Kd;

    private BufferedImage map_Ks;

    public Material(String mtrLibName) {
        this.name = mtrLibName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float[] getKa() {
        return Ka;
    }

    public void setKa(float[] ka) {
        Ka = ka;
    }

    public float[] getKd() {
        return Kd;
    }

    public void setKd(float[] kd) {
        Kd = kd;
    }

    public boolean isD() {
        return d;
    }

    public void setD(boolean d) {
        this.d = d;
    }

    public float getTr() {
        return Tr;
    }

    public void setTr(float tr) {
        Tr = tr;
    }

    public float getIllum() {
        return illum;
    }

    public void setIllum(float illum) {
        this.illum = illum;
    }

    public float[] getKs() {
        return Ks;
    }

    public void setKs(float[] ks) {
        Ks = ks;
    }

    public int getNs() {
        return Ns;
    }

    public void setNs(int ns) {
        Ns = ns;
    }

    public BufferedImage getMap_Kd() {
        return map_Kd;
    }

    public void setMap_Kd(String map_Kd) {
        ClassLoader classLoader = getClass().getClassLoader();
        try(InputStream inputStream = classLoader.getResourceAsStream(map_Kd)) {
            if (inputStream != null) {
                this.map_Kd = ImageIO.read(inputStream);
            } else {
                System.out.println("file: " + map_Kd + " not found");
            }
        } catch(IOException ex) {
            throw new RuntimeException();
        }
    }

    public BufferedImage getMap_Ks() {

        return map_Ks;
    }

    public void setMap_Ks(String map_Ks) {

        ClassLoader classLoader = getClass().getClassLoader();
        try(InputStream inputStream = classLoader.getResourceAsStream(map_Ks)) {
            if (inputStream != null) {
                this.map_Kd = ImageIO.read(inputStream);
            } else {
                System.out.println("file: " + map_Kd + " not found");
            }
        } catch(IOException ex) {
            throw new RuntimeException();
        }
    }
}
