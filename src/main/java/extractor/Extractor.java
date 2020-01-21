package extractor;

import links.Link;
import org.citygml4j.geometry.BoundingBox;
import org.citygml4j.model.citygml.building.AbstractBoundarySurface;
import org.citygml4j.model.citygml.building.AbstractBuilding;
import org.citygml4j.model.citygml.core.AbstractCityObject;
import org.citygml4j.model.citygml.core.CityModel;
import org.citygml4j.model.citygml.core.CityObjectMember;
import org.citygml4j.model.gml.geometry.AbstractGeometry;
import org.citygml4j.model.gml.geometry.primitives.AbstractSolid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Objects.isNull;

public class Extractor {
    private CityModel city;
    private Map<String,AbstractGeometry> shapes;
    private List<Link> links;

    public Extractor(CityModel city) {
        this.city = city;
        this.links = new ArrayList<Link>();
        this.shapes = new HashMap<String, AbstractGeometry>();
    }

    public List<Link> extract() {
        parseNodes();

        Map<String, BoundingBox> boxes = Geometry.checkRelations(shapes);

        generateLinks(boxes);

    return links;
    }

    private void parseNodes() {
        List<CityObjectMember> members = city.getCityObjectMember();

        for (CityObjectMember member : members) {
            parseObject(member.getCityObject());
        }
    }

    private void parseObject(AbstractCityObject object) {
        String id = object.getId();

        if (object instanceof AbstractBuilding) {
            AbstractBuilding b = (AbstractBuilding) object;
            if (!isNull(b.getLod2Solid())) {
                AbstractSolid shape = b.getLod2Solid().getSolid();
                shapes.put(id, shape);
            }
        } else
        if (object instanceof AbstractBoundarySurface) {
            //todo... do something else...
        }
    }

    private void generateLinks(Map<String,BoundingBox> boxes) {
        for (Map.Entry<String, BoundingBox> entry : boxes.entrySet()) {
            Link l = new Link(entry.getKey(), entry.getValue().toList().toString());
            links.add(l);
        }
    }
}
