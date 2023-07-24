import java.util.ArrayList;
import java.util.LinkedHashMap;

public class TT extends Algorithm{
    int yes_count;
    public TT (KnowledgeBase kb) {
        super(kb);
        super.name = "TT";
        yes_count = 0;
    }

    @Override
    public ArrayList<String> calculate_result() {
        ArrayList<String> result = new ArrayList<>();
        LinkedHashMap<String, Boolean> model = new LinkedHashMap<>();
        if(TT_check_all(this.kb,model)) {
            result.add("YES");
            result.add(": ");
            result.add(Integer.toString(yes_count) );
        } else {
            result.add("NO");
            result.add(": ");
            result.add(Integer.toString(yes_count) );
        }

//        System.out.println(result.size());
        return result;


    }

    public boolean TT_check_all(KnowledgeBase kb,LinkedHashMap<String, Boolean> model) {
//        System.out.println("gg");
        if(kb.symbol_list.isEmpty()) {
            KB_fill(kb,model);
            for(String key: model.keySet()) {
                System.out.println(key+model.get(key));

            }
            System.out.println("__________________");

            if(kb.KB_checking(kb.KB_root)) {
                for(String key: model.keySet()) {
                    if(key.equals(key)){
                        this.yes_count ++;
                        return true;
                    } else {
                        return false;
                    }

                }
                return false;
//                System.out.println(kb.target);
//                if(model.get(kb.target)) {
//                    this.yes_count ++;
//                    return true;
//                } else {
//                    return false;
//                }


            } else {
                return true;
            }
        } else {
            String symbol = kb.symbol_list.get(0);

            kb.symbol_list.remove(0);
            System.out.println(kb.symbol_list.size());
            LinkedHashMap<String, Boolean> model_false = (LinkedHashMap<String, Boolean>)model.clone();
            KnowledgeBase kb_false = kb.fake_clone();
            kb_false.symbol_list = (ArrayList<String>) kb.symbol_list.clone();
            model_false.put(symbol, false);
            LinkedHashMap<String, Boolean> model_true = (LinkedHashMap<String, Boolean>)model.clone();
            KnowledgeBase kb_true = kb.fake_clone();
            kb_true.symbol_list = (ArrayList<String>) kb.symbol_list.clone();
            model_true.put(symbol, true);


            return (TT_check_all(kb_false,model_false) & TT_check_all(kb_true,model_true));

        }


    }

    public void KB_fill (KnowledgeBase kb,LinkedHashMap<String, Boolean> model) {
        for(String key: model.keySet()){
            kb.add_value(key,model.get(key));
        }

    }



}
