package extractor.geometric;

import javafx.util.Pair;
import links.Link;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.citygml4j.geometry.BoundingBox;
import org.citygml4j.model.gml.geometry.AbstractGeometry;
import org.jetbrains.annotations.NotNull;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.operation.distance.DistanceOp;

import java.util.*;

public class ProximityOperations {
    private static final Logger LOGGER = LogManager.getLogger();

    /**
     * Distance threshold, over which, objects are discarded
     */
    public static double MAX_DIST = 0;

    /**
     * This method calculates the pairwise distance between
     * all the geometries provided.
     * @param shapes the list of abstract geometries of city objects
     * @return it returns a list of triplets (id, id, distance)  of couples
     *         of objects having a distance > than the DISTANCE_THRESHOLD
     */
    @NotNull
    public static List<Link>
    checkNearEnough(Map<String, AbstractGeometry> shapes) {
        List<Pair<String,Geometry>> geometries = toGeometries(shapes);

        return compute(geometries);
    }

    /**
     * This method takes a list of citygml4j spatial objects and converts them
     * into JTS geometries, but keeping a reference to the CityGML thematic
     * object they were extracted from.
     * @param shapes spatial objects of the source CityGML model
     * @return returns a list of JTS geometries, with a reference to the
     *         source objects from which they were extracted.
     */
    @NotNull
    private static List<Pair<String, Geometry>>
    toGeometries(@NotNull Map<String, AbstractGeometry> shapes) {

        List<Pair<String, Geometry>> geometries = new ArrayList<>();
        GeometryFactory factory = new GeometryFactory();

        // For every CityGML shape...
        for (Map.Entry<String, AbstractGeometry> entry : shapes.entrySet()) {
            // 1 - Approximate them with their bounding box...
            BoundingBox box = entry.getValue().calcBoundingBox();
            // 2 - Convert them to JTS Geometries...
            Geometry geometry = factory.toGeometry(toEnvelope(box));

            // 3- Store the result in a list of <BuildingId, Geometry>...
            geometries.add(new Pair<>(entry.getKey(), geometry));
        }

        return geometries;
    }

    /**
     * This method is the core of the distance computation:
     * it computes the pairwise distance between every pair of objects
     * of the list.
     * If they are closer than MAX_DIST, it keeps track of them.
     * @param geometries objects of which we are evaluating the distance
     * @return a set of links corresponding to near enough objects.
     */
    @NotNull
    private static List<Link> compute(@NotNull List<Pair<String, Geometry>> geometries) {
        List<Link> near = new ArrayList<>();
        for (int i = 0; i < geometries.size(); i++) {
            for(int j = i + 1; j < geometries.size(); j++) {
                Pair<String, Geometry> g1 = geometries.get(i);
                Pair<String, Geometry> g2 = geometries.get(j);

                double d = DistanceOp.distance(g1.getValue(), g2.getValue());
                if(d > MAX_DIST)
                    LOGGER.trace("Geometries:<" + g1.getKey() + "> and <" +
                            g2.getKey() + "> are " + (d - MAX_DIST) + " too far!");
                else {
                    LOGGER.trace("Geometries:<" + g1.getKey() + "> and <" +
                            g2.getKey() + "> are touching!");

                    String id = g1.getKey() + "@@" + g2.getKey();
                    near.add(new Link(id, "topoNear",
                            d, toNodes(g1.getKey(), g2.getKey())));
                }
            }
        }
        return near;
    }

    /**
     * Given two strings representing the IDs of the objects,
     * it returns a list containing them.
     * @param n1 first ID
     * @param n2 second ID
     * @return list of IDs
     */
    @NotNull
    private static List<String> toNodes(String n1, String n2) {
        List<String> ns = new ArrayList<>();
        ns.add(n1);
        ns.add(n2);
        return ns;
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
