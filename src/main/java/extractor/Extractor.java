package extractor;

import org.citygml4j.model.citygml.building.AbstractBuilding;
import org.citygml4j.model.citygml.building.BuildingPart;
import org.citygml4j.model.citygml.building.BuildingPartProperty;
import org.citygml4j.model.citygml.core.AbstractCityObject;
import org.citygml4j.model.citygml.core.CityModel;
import org.citygml4j.model.citygml.core.CityObjectMember;
import org.citygml4j.model.gml.geometry.primitives.AbstractSolid;
import org.apache.commons.lang.NotImplementedException;
import org.citygml4j.model.gml.geometry.primitives.SolidProperty;

import java.util.List;

import static java.util.Objects.isNull;

/**
 * Library Class to extract topological links from City Model.
 * It stores the city model, so that multiple extractions can be executed.
 */
public class Extractor {
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
        return strategy.results();
    }

    /**
     * Method that propagates the geometric evaluation to children
     * @param strategy the evaluation strategy which carries the
     *                 geometrical semantics of the operation
     */
    private void traverseNodes(GeometricStrategy strategy) {
        List<CityObjectMember> members = city.getCityObjectMember();

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
        if (obj instanceof AbstractBuilding) {
            AbstractBuilding b = (AbstractBuilding) obj;
            parseBuilding(b, str);
        } else
        //if (object instanceof AbstractBoundarySurface) {
            throw new NotImplementedException("Unsupported City Object!");
        //}
    }



    private void parseBuilding(AbstractBuilding b, GeometricStrategy str) {
        if (!isNull(b.getLod2Solid())) {
            parseSolid(b.getId(), b.getLod2Solid(), str);
        } else if (b.isSetConsistsOfBuildingPart()) {
            List<BuildingPartProperty> ps = b.getConsistsOfBuildingPart();
            for (BuildingPartProperty p : ps) {
                BuildingPart bp = p.getBuildingPart();
                if (!isNull(bp.getLod2Solid())) {
                    parseSolid(bp.getId(), bp.getLod2Solid(), str);
                } else if (!isNull(bp.getLod1Solid())) {
                    System.out.println("WARNING: Ignoring LOD1-only object!");
                } else
                    throw new NotImplementedException
                            ("Unsupported geometry LOD at BuildingPart@"
                                    + bp.getId());
            }
        } else
            throw new NotImplementedException
                    ("Unsupported geometry LOD at Building@" + b.getId());
    }

    private void parseSolid(String id, SolidProperty shape,
                            GeometricStrategy str) {
        AbstractSolid s = shape.getSolid();
        str.evaluate(id, s);
    }



}
