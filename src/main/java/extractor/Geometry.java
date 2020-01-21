package extractor;

import org.citygml4j.geometry.BoundingBox;
import org.citygml4j.model.gml.geometry.AbstractGeometry;
import org.locationtech.jts.geom.CoordinateXY;
import org.locationtech.jts.geom.Coordinates;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class Geometry {

    public static Map<String,BoundingBox> checkRelations(Map<String, AbstractGeometry> shapes) {
        return checkNearEnough(shapes);
    }

    private static Map<String,BoundingBox>
        checkNearEnough(Map<String, AbstractGeometry> shapes) {

        Map<String,BoundingBox> boxes = new HashMap<String, BoundingBox>();

        for (Map.Entry<String, AbstractGeometry> entry : shapes.entrySet()) {
            BoundingBox box = entry.getValue().calcBoundingBox();
            boxes.put(entry.getKey(), box);
            //List<Double> lCoords = box.getLowerCorner();
            //Coordinates coords = new Coordinates(lCoords.get(0));
        }

        return boxes;

    }

    private static Coordinates toCoords(BoundingBox box) {
        CoordinateXY coords = (CoordinateXY) Coordinates.create(2);
        //coords.setX(box.getLowerCorner(), cs.get(1));
        return null;
    }
}
