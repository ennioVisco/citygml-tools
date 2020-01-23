package extractor.geometric;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javatuples.Triplet;
import org.citygml4j.geometry.BoundingBox;
import org.citygml4j.model.gml.geometry.AbstractGeometry;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.operation.distance.DistanceOp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GeometryOperations {

    public static final double DISTANCE_THRESHOLD = 0;
    private static final Logger LOGGER = LogManager.getLogger();

    /**
     * This method calculates the pairwise distance between
     * all the geometries provided.
     * @param shapes the list of abstract geometries of city objects
     * @return it returns a list of triplets (id, id, distance)  of couples
     *         of objects having a distance > than the DISTANCE_THRESHOLD
     */
    public static List<Triplet<String, String, Double>>
    checkNearEnough(Map<String, AbstractGeometry> shapes) {
        Map<String,Geometry> geometries = extractGeometries(shapes);

        return compute(geometries);
    }

    private static List<Triplet<String, String, Double>>
    compute(Map<String, Geometry> geometries) {
        List<Triplet<String, String, Double>> near = new ArrayList<>();
        for (Map.Entry<String, Geometry> g1 : geometries.entrySet()) {
            for (Map.Entry<String, Geometry> g2 : geometries.entrySet()) {
                double dist = DistanceOp.distance(g1.getValue(), g2.getValue());
                if(dist > DISTANCE_THRESHOLD) {
                    LOGGER.trace("Geometries:<" + g1.getKey() + "> and <" + g2.getKey() + "> are " + dist + " far!");
                    near.add(new Triplet<>(g1.getKey(), g2.getKey(), dist));
                } else
                    LOGGER.trace("Geometries:<" + g1.getKey() + "> and <" + g2.getKey() + "> are touching!");
            }
        }
        return near;
    }

    private static Map<String, Geometry>
    extractGeometries(Map<String, AbstractGeometry> shapes) {
        Map<String, Geometry> geometries = new HashMap<>();
        GeometryFactory factory = new GeometryFactory();

        for (Map.Entry<String, AbstractGeometry> entry : shapes.entrySet()) {
            BoundingBox box = entry.getValue().calcBoundingBox();
            Geometry geometry = factory.toGeometry(toEnvelope(box));

            geometries.put(entry.getKey(), geometry);
        }

        return geometries;
    }

    /**
     * This method takes a citygml4j BoundingBox and converts it to
     * a JTS Envelope
     * @param box the BoundingBox of interest
     * @return the corresponding Envelope
     */
    private static Envelope toEnvelope(BoundingBox box) {
        return new Envelope(box.getLowerCorner().getX(),
                            box.getUpperCorner().getX(),
                            box.getLowerCorner().getY(),
                            box.getUpperCorner().getY());
    }

}
