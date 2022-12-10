package org.bsuir.graphics.parser;

import org.bsuir.graphics.model.Material;

import java.util.ArrayList;
import java.util.List;

public class MtlParser {

    private final List<Material> materials = new ArrayList<>();
    private String curMaterialName;

    public void onNewMtl(String mtrLibName) {
        this.curMaterialName = mtrLibName;
        materials.add(new Material(mtrLibName));
    }

    public void onKAmbient(float[] floats) {
        findMaterialByCurName().setKa(floats);
    }

    public void onKDiffuse(float[] floats) {
        findMaterialByCurName().setKd(floats);
    }

    public void onKSpecular(float[] floats) {
        findMaterialByCurName().setKs(floats);
    }

    public void onNSpecular(int value) {
        findMaterialByCurName().setNs(value);
    }

    private final Material findMaterialByCurName(){
        return materials.stream()
                .filter(m -> m.getName().equals(curMaterialName))
                .findFirst().orElseThrow(RuntimeException::new);
    }

    public List<Material> getMaterials() {
        return materials;
    }

    public void onDiffuseMap(String map) {
        findMaterialByCurName().setMap_Kd(map);
    }
}
