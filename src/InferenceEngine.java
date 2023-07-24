import java.util.ArrayList;

public class InferenceEngine {
    //    initialize values
    KnowledgeBase kb;
    Algorithm main_algo;
    ArrayList<Algorithm> algo_list = new ArrayList<Algorithm>();

    //    constructor
    public InferenceEngine (KnowledgeBase kb, String algo_name) {
//        add algorithms to the algo list

        this.kb = kb;
        Algorithm TT = new TT(this.kb);
        algo_list.add(TT);
        this.main_algo = algo_lookup(algo_name);

    }

    //    get the return path from the chosen algorithm
    public ArrayList<String> get_result() {
//        System.out.println("gg");
        ArrayList<String> result;
        if(main_algo!= null) {
            result = main_algo.calculate_result();

        } else {
            result = new ArrayList<>();
            result.add("there is no matching algorithm name");
        }
        return result;
    }
    // find the algorithm from the input string algo_name
    public Algorithm algo_lookup(String algo_name) {
        Algorithm result = null;
        for(Algorithm algo:algo_list) {

            if (algo.name.equals(algo_name)) {
                result = algo;
            }
        }
        return result;
    }
}
