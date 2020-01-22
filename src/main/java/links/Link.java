package links;

import extractor.geometric.GeometricObject;

import java.util.ArrayList;
import java.util.List;

public final class Link implements GeometricObject {
    public String Id;
    public String Type;
    public double Distance;
    public List<Node> Nodes;

    public Link(String id, String type) {
        this(id, type, 0, new ArrayList<Node>());
    }

    public Link(String id, String type, double distance, List<Node> nodes) {
        Id = id;
        Type = type;
        Distance = distance;
        Nodes = nodes;
    }

    @Override
    public String toString() {
        return "\n\t{\n\t\t id:" + Id +
                    "\n\t\t type:" + Type +
                    "\n\t\t distance:" + Distance +
                    "\n\t\t nodes:" + Nodes +
                "\n\t}\n\t";
    }
}

