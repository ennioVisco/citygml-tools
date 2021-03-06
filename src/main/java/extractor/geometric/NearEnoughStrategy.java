package extractor.geometric;

import links.Link;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.citygml4j.model.gml.geometry.AbstractGeometry;
import org.citygml4j.model.gml.geometry.primitives.AbstractSolid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The NearEnoughStrategy is a GeometricStrategy that is called with the goal
 * of evaluating when objects of the model satisfy a predefined "NearEnough"
 * relation.
 * The strategy works in two steps:
 * 1 - For each city object, we collect their shape and label them with the
 *     object id.
 * 2 - After that, we generate a set of links corresponding to the objects that
 *     are "NearEnough" and return the list of links as a result.
 *
 * @see GeometricStrategy for information about how these strategies are called
 */
public class NearEnoughStrategy implements GeometricStrategy {
    private static final Logger LOGGER = LogManager.getLogger();

    private Map<String, AbstractGeometry> shapes;
    private int distance = 0;

    public NearEnoughStrategy(int distance) {
        this.shapes = new HashMap<>();
        this.distance = distance;
    }

    /**
     * It takes a city object's id and shape
     * and adds them to the list of objects to consider
     * @param id the id of a city object
     * @param shape the shape of a city object
     */
    @Override
    public void evaluate(String id, AbstractSolid shape) {
        shapes.put(id, shape);
    }

    /**
     * Given the list of shapes collected so far, it checks for relations:
     * if they are present, a link is added to another list
     * @return the list of generated links
     */
    @Override
    public List<? extends GeometricObject> results() {
        LOGGER.debug("N. of Shapes:" + shapes.size());

        ProximityOperations.MAX_DIST = distance;
        List<Link> geometries = ProximityOperations.checkNearEnough(shapes);

        LOGGER.debug("Computed Relations.");
        shapes = null; //Trying to optimize garbage collection.

        return geometries;
    }
}
