package extractor;

import org.citygml4j.model.gml.geometry.primitives.AbstractSolid;

import java.util.List;

public interface GeometricStrategy {

    void evaluate(String id, AbstractSolid shape);

    List<? extends GeometricObject> results();
}
