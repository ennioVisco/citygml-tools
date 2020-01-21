package links;

import java.util.ArrayList;
import java.util.List;

public final class Link {
    public String Id;
    public String Type;
    public List<Node> Nodes;

    public Link(String id, String type) {
        this(id, type, new ArrayList<Node>());
    }

    public Link(String id, String type, List nodes) {
        Id = id;
        Type = type;
        Nodes = nodes;
    }

    @Override
    public String toString() {
        return "\n\t{\n\t\t id:"+ Id + "\n\t\t type:" + Type + "\n\t\t nodes:" + Nodes + "\n\t}\n\t";
    }
}

