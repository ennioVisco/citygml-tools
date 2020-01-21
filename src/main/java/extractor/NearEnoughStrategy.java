package extractor;

import links.Link;
import org.citygml4j.geometry.BoundingBox;
import org.citygml4j.model.gml.geometry.AbstractGeometry;
import org.citygml4j.model.gml.geometry.primitives.AbstractSolid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NearEnoughStrategy implements GeometricStrategy {
    private Map<String, AbstractGeometry> shapes;
    private List<Link> links;

    public NearEnoughStrategy() {
        this.links = new ArrayList<>();
        this.shapes = new HashMap<>();
    }


    @Override
    public void evaluate(String id, AbstractSolid shape) {
        shapes.put(id, shape);
    }

    @Override
    public List<? extends GeometricObject> results() {
        Map<String, BoundingBox> boxes = Geometry.checkRelations(shapes);

        generateLinks(boxes);

        return links;
    }

    private void generateLinks(Map<String,BoundingBox> boxes) {
        for (Map.Entry<String, BoundingBox> entry : boxes.entrySet()) {
            Link l = new Link(entry.getKey(), entry.getValue().toList().toString());
            links.add(l);
        }
    }
}
