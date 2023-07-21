import java.util.ArrayList;
import java.util.List;

public class Vertex {

    List<Node> children = new ArrayList<>();
    Node parent;
    String value;

    Boolean negation = false;
}
