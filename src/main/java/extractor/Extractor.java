package extractor;

import extractor.geometric.GeometricObject;
import extractor.geometric.GeometricStrategy;
import org.citygml4j.model.citygml.building.AbstractBuilding;
import org.citygml4j.model.citygml.building.BuildingPart;
import org.citygml4j.model.citygml.building.BuildingPartProperty;
import org.citygml4j.model.citygml.core.AbstractCityObject;
import org.citygml4j.model.citygml.core.CityModel;
import org.citygml4j.model.citygml.core.CityObjectMember;
import org.citygml4j.model.gml.geometry.primitives.AbstractSolid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.commons.lang.NotImplementedException;
import org.citygml4j.model.gml.geometry.primitives.SolidProperty;

import java.util.List;

import static java.util.Objects.isNull;

/**
 * Library Class to extract topological links from City Model.
 * It stores the city model, so that multiple extractions can be executed.
 */
public class Extractor {
    private static final Logger LOGGER = LogManager.getLogger();

    private CityModel city;

    public Extractor(CityModel city) {
        this.city = city;
    }

    /**
     * Method that activates the geometric evaluation strategy and
     * returns its results
     * @param strategy the evaluation strategy which carries the
     *                 geometrical semantics of the operation
     * @return it returns a list of objects meaningful to the
     *         geometric strategy adopted
     */
    public List<? extends GeometricObject> extract(GeometricStrategy strategy) {
        traverseNodes(strategy);
        LOGGER.debug("Nodes traversed, storing results.");
        return strategy.results();
    }

    /**
     * Method that propagates the geometric evaluation to children
     * @param strategy the evaluation strategy which carries the
     *                 geometrical semantics of the operation
     */
    private void traverseNodes(GeometricStrategy strategy) {
        List<CityObjectMember> members = city.getCityObjectMember();

        LOGGER.debug("N. of City Objects:" + members.size());

        for (CityObjectMember member : members) {
            parseObject(member.getCityObject(), strategy);
        }
    }

    /**
     * Routing method that selects the appropriate behavior depending on the
     * type of CityGML object.
     * @param obj the abstract city object being evaluated
     * @param str the evaluation strategy which carries the
     *            geometrical semantics of the operation
     */
    private void parseObject(AbstractCityObject obj, GeometricStrategy str) {
        if (obj instanceof AbstractBuilding)
            parseBuilding((AbstractBuilding) obj, str);
        else
            throw new NotImplementedException("Unsupported City Object!");
    }


    /**
     * Extracts the geometric shapes of the given building and calls <Code>ParseSolid</Code>
     * @param b CityGML Building object to parse
     * @param str the Strategy to perform over the object
     */
    private void parseBuilding(AbstractBuilding b, GeometricStrategy str) {
        // Prioritize LOD2 first-level geometry
        if (!isNull(b.getLod2Solid())) {
            evalSolid(b.getId(), b.getLod2Solid(), str);
        // Otherwise, check whether the building is made of Building Parts
        } else if (b.isSetConsistsOfBuildingPart()) {
            List<BuildingPartProperty> ps = b.getConsistsOfBuildingPart();

            for (BuildingPartProperty p : ps) {
                BuildingPart bp = p.getBuildingPart();
                if (!isNull(bp.getLod2Solid()))
                    evalSolid(bp.getId(), bp.getLod2Solid(), str);
                else if (!isNull(bp.getLod1Solid()))
                    LOGGER.warn("Ignoring LOD1-only object!");
                else
                    throw new NotImplementedException
                            ("Unsupported geometry LOD at BuildingPart@"
                                    + bp.getId());
            }
        } else
            throw new NotImplementedException
                    ("Unsupported geometry LOD at Building@" + b.getId());
    }

    /**
     * Parser for gml:solid object.
     * It takes the geometric object and evaluates the given
     * geometric strategy over it.
     * @param id Reference to the object to which the geometry is related
     * @param sp the 3D geometric object
     * @param str the Geometric Strategy to evaluate
     */
    private void evalSolid(String id, SolidProperty sp, GeometricStrategy str) {
        AbstractSolid s = sp.getSolid();
        str.evaluate(id, s);
    }



}
