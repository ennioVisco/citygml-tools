package links;

import extractor.geometric.GeometricObject;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public final class Link implements GeometricObject {
    public String Id;
    public String Type;
    public double Distance;
    public List<String> Nodes;

    public static String presentation;

    public Link(String id, String type) {
        this(id, type, 0, new ArrayList<>());
    }

    public Link(String id, String type, double distance, List<String> nodes) {
        Id = id;
        Type = type;
        Distance = distance;
        Nodes = nodes;
    }

    @Override
    public String toString() {
        if("ADE".equals(presentation))
            return toADE();
        else
            return toRaw();
    }

    @NotNull
    @Contract(pure = true)
    public String toRaw() {
        return "\n\t{\n\t\t id:" + Id +
                "\n\t\t type:" + Type +
                "\n\t\t distance:" + Distance +
                "\n\t\t nodes:" + Nodes +
                "\n\t}\n\t";
    }

    @NotNull
    public String toADE() {
        //TODO: add distance to topoRelation
        return "\t<topology:topoRelation>\n" +
                    "\t\t<topology:" + Type + " gml:id=\"" + Id + "\">\n" +
                        getNodes() +
                    "\t\t</topology:" + Type + ">\n" +
                "\t</topology:topoRelation>\n";

    }

    @NotNull
    public String getNodes() {
        StringBuilder nodes = new StringBuilder();

        for(String b : Nodes)
            nodes.append("\t\t\t<topology:topoBuilding xlink:href=\"")
                    .append(b).append("\" />\n");

        return nodes.toString();
    }
}

