package extractor.geometric;

import javafx.util.Pair;
import links.Link;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.citygml4j.geometry.BoundingBox;
import org.citygml4j.model.gml.geometry.AbstractGeometry;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.operation.distance.DistanceOp;

import java.util.*;

public class GeometryOperations {

    public static double DISTANCE_THRESHOLD;
    private static final Logger LOGGER = LogManager.getLogger();

    /**
     * This method calculates the pairwise distance between
     * all the geometries provided.
     * @param shapes the list of abstract geometries of city objects
     * @return it returns a list of triplets (id, id, distance)  of couples
     *         of objects having a distance > than the DISTANCE_THRESHOLD
     */
    public static List<Link>
    checkNearEnough(Map<String, AbstractGeometry> shapes) {
        List<Pair<String,Geometry>> geometries = extractGeometries(shapes);

        return compute(geometries);
    }

    private static List<Link> compute(List<Pair<String, Geometry>> geometries) {
        List<Link> near = new ArrayList<>();
        for (int i = 0; i < geometries.size(); i++) {
            for(int j = i + 1; j < geometries.size(); j++) {
                Pair<String, Geometry> g1 = geometries.get(i);
                Pair<String, Geometry> g2 = geometries.get(j);
                double dist = DistanceOp.distance(g1.getValue(), g2.getValue());
                if(dist > DISTANCE_THRESHOLD) {
                    LOGGER.trace("Geometries:<" + g1.getKey() + "> and <" +
                            g2.getKey() + "> are " + dist + " far!");
                    String id = g1.getKey() + "@@" + g2.getKey();

                    near.add(new Link(id,"NearEnoughRelation",
                            dist, nodes(g1.getKey(),g2.getKey())));
                } else
                    LOGGER.trace("Geometries:<" + g1.getKey() + "> and <" +
                            g2.getKey() + "> are touching!");
            }
        }
        return near;
    }

    private static List<String> nodes(String n1, String n2) {
        List<String> ns = new ArrayList<>();
        ns.add(n1);
        ns.add(n2);
        return ns;
    }

    private static List<Pair<String, Geometry>>
    extractGeometries(Map<String, AbstractGeometry> shapes) {
        List<Pair<String, Geometry>> geometries = new ArrayList<>();
        GeometryFactory factory = new GeometryFactory();

        for (Map.Entry<String, AbstractGeometry> entry : shapes.entrySet()) {
            BoundingBox box = entry.getValue().calcBoundingBox();
            Geometry geometry = factory.toGeometry(toEnvelope(box));

            geometries.add(new Pair(entry.getKey(), geometry));
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
