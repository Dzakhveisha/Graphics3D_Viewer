package org.bsuir.graphics.light;

import org.bsuir.graphics.model.DataReference;
import org.bsuir.graphics.model.Normal;
import org.bsuir.graphics.model.Vertex;

import java.awt.*;
import java.util.List;

public class LambLight {
    
    private final double radians = 3.14159; 

    private final Vertex theSun = new Vertex(2.5f, 0, -5);

    private final List<Normal> normalList;

    public LambLight(List<Normal> normalList) {

        this.normalList = normalList;
        float length = (float) Math.sqrt(theSun.getElement(0) * theSun.getElement(0)
            + theSun.getElement(1) * theSun.getElement(1) + theSun.getElement(2) * theSun.getElement(2));
        theSun.setElement(0, theSun.getElement(0) * 1 / length);
        theSun.setElement(1, theSun.getElement(1) * 1 / length);
        theSun.setElement(2, theSun.getElement(2) * 1 / length);
        System.out.println(length);
    }

    public Color calcLightness(List<DataReference> dataReferenceList) {

        double avgColor = 0;
        for (DataReference dataReference : dataReferenceList) {
            Normal normal = normalList.get(dataReference.normalIndex - 1);
            float num = normal.x * theSun.x + normal.y * theSun.y + normal.z * theSun.z;
            double den = (
                Math.sqrt(
                    Math.pow(normal.x, 2) + Math.pow(normal.y, 2) + Math.pow(normal.z, 2)
                ) * (
                    Math.sqrt(Math.pow(theSun.x, 2) + Math.pow(theSun.y, 2) + Math.pow(theSun.z, 2))
                )
            );
            double cos = num / den;
            avgColor += Math.acos(cos);
        }
        avgColor = avgColor / dataReferenceList.size();

      //  System.out.println(avgColor);
        avgColor = (avgColor * 255) / radians;
        Color returnColor;

        returnColor = new Color((int) avgColor, (int) avgColor, (int) avgColor);
        return returnColor;
    }
}
