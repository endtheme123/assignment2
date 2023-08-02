import java.util.ArrayList;
import java.util.LinkedHashMap;

public class TT extends Algorithm {
    LinkedHashMap<String, Boolean> truth_model;
    int yes_count;

    public TT(KnowledgeBase kb) {
//        initialize the truth table algorithm
        super(kb);
        super.name = "TT";
        yes_count = 0;
        this.truth_model = new LinkedHashMap<>();// this one is used for testing
    }

    @Override
    public ArrayList<String> calculate_result() {
//        create a new arraylist to store the result
        ArrayList<String> result = new ArrayList<>();
//        initialize an empty model for the algorithm
        LinkedHashMap<String, Boolean> model = new LinkedHashMap<>();
//        invoke the algorithm
        if (TT_check_all(this.kb, model)) {
            result.add("YES");
            result.add(": ");
            result.add(Integer.toString(yes_count));
        } else {
            result.add("NO");
            result.add(": ");
            result.add(Integer.toString(yes_count));
        }

//        System.out.println(result.size());
        return result;


    }

    public boolean TT_check_all(KnowledgeBase kb, LinkedHashMap<String, Boolean> model) {

//        System.out.println("gg");
//        if the symbol is  empty
        if (kb.symbol_list.isEmpty()) {
//            fill the KB with the symbol saved inside the table called model
            KB_fill(kb, model);


//            run KB_checking to get the logical value of the whole KB
            if (kb.KB_checking(kb.KB_root)) {

//                    also run KB checking on the target to check if the target is true or not if both KB and the target is true => entailment = true
                    if (kb.KB_checking(kb.target)) {
//                        if the entailment is true => increase the count
                        this.yes_count++;

                        truth_model = model;
                        return true;
                    }

//                if not, return false
                return false;


            } else {
//                if the KB is false => return true (to keep the algorithm true) but not increase the count
                return true;
            }
        } else {
//            if the symbol list is not empty yet => keep adding the symbol into the table
            String symbol = kb.symbol_list.get(0);

            kb.symbol_list.remove(0);

//            all of these data structure will need to be cloned to avoid cloning problem
            LinkedHashMap<String, Boolean> model_false = (LinkedHashMap<String, Boolean>) model.clone();
            KnowledgeBase kb_false = kb.fake_clone();
            kb_false.symbol_list = (ArrayList<String>) kb.symbol_list.clone();
            model_false.put(symbol, false);
            LinkedHashMap<String, Boolean> model_true = (LinkedHashMap<String, Boolean>) model.clone();
            KnowledgeBase kb_true = kb.fake_clone();
            kb_true.symbol_list = (ArrayList<String>) kb.symbol_list.clone();
            model_true.put(symbol, true);


            return (TT_check_all(kb_false, model_false) & TT_check_all(kb_true, model_true));

        }


    }

//    this function is used to fill logical value into the knowledge base
    public void KB_fill(KnowledgeBase kb, LinkedHashMap<String, Boolean> model) {
        for (String key : model.keySet()) {
            kb.add_value(key, model.get(key));
        }

    }


}
