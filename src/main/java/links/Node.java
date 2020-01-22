package links;

public final class Node {
    public final String Id;
    public final String Type;

    public Node(String id, String type) {
        Id = id;
        Type = type;
    }

    public static Node fromId(String id) {
        return new Node(id, "UNKNOWN_NODE_TYPE");
    }
}
