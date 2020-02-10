package extractor.geometric;

import org.citygml4j.model.gml.geometry.primitives.AbstractSolid;

import java.util.List;

/**
 * The GeometricStrategy provides the common interface for operations to be
 * performed over the objects of the spatial model of a CityGML source.
 *
 * Based on a side-effectful model, it provides two basic primitives:
 * - evaluate: performs a computation (defined by the implementation) over the
 *             given object. Results are stored internally (if applicable)
 * - results: returns a list of results of preceding evaluations.
 */
public interface GeometricStrategy {

    /**
     * This method takes a geometric object and a referencing ID and performs
     * some geometric analysis over it.
     * @param id Referenced object of the current geometrical object.
     * @param shape Current geometrical object
     */
    void evaluate(String id, AbstractSolid shape);

    /**
     * This method returns a list of objects after they have been evaluated
     * @return a list of geometric objects of some specific kind
     */
    List<? extends GeometricObject> results();
}
