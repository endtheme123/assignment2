import java.util.ArrayList;

public abstract class Algorithm {
    String name;
    KnowledgeBase kb;

    public Algorithm(KnowledgeBase kb) {this.kb = kb;}
    public abstract ArrayList<String> calculate_result();
}
