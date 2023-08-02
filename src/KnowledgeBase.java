import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Integer.parseInt;
import static java.util.Arrays.stream;

public class KnowledgeBase {
//    define class variables for the class

    ArrayList<String> symbol_list; // => list of symbols in the knowledge base

    ArrayList<Node> truth_list; //=> list of ground truth (mostly for BC and FC)

    ArrayList<ArrayList<Node>> logic_trees;//=> List to save the nodes in the tree, results of tokenization
    ArrayList<Clause> clause_list; //=> list of clauses (for BC and FC)

    String filename; //=> filename input from parameter
    Node target; //=> queries
    String[] operators; //=> use to save a list of operators (mostly used in replace function loop)
    Node KB_root; //=> save the root of the tree generated from the knowledge base
    ArrayList<Integer> remove_count; //=> list of index (check point to use sublist => uses to read bracketed content)

    public KnowledgeBase(String filename) {
//        initializing
        this.logic_trees = new ArrayList<>();
        this.remove_count = new ArrayList<>();
        this.symbol_list = new ArrayList<>();
        this.clause_list = new ArrayList<>();
        this.truth_list = new ArrayList<>();
        this.filename = filename;
        operators = new String[]{ "\\<\\=\\>","\\=\\>", "\\&", "\\|\\|", "\\)", "\\("};
        GenerateKB(filename);
    }

    void GenerateKB(String filepath) {
//        reading file
        try {
//            read the filename
            File myObj = new File(filepath);
//            read the file
            Scanner myReader = new Scanner(myObj);
//            read the first line + check if it is written in correct format or not

            String isTell = myReader.nextLine();
            if (isTell.trim().equals("TELL")) {
//                   read the knowledge base
                String KB_input = myReader.nextLine().replaceAll(" ", ""); //=> trim the input

//                create a copy of the input kb string
                String KB_input_1 = KB_input;

//                extract the symbols from the string
                for (String o : this.operators) {
                    KB_input_1 = KB_input_1.replaceAll(o, ";").replaceAll("~", "");
                }
                while(KB_input_1.contains(";;")) {
                    KB_input_1 = KB_input_1.replaceAll(";;",";");
                }
                while(KB_input_1.charAt(0) == ';') {
                    KB_input_1 = KB_input_1.substring(1,KB_input_1.length()-1);
                }


                ArrayList<String> entities = new ArrayList<>(Arrays.asList(KB_input_1.split(";")));
//                entities.remove(entities.size() - 1);

//                add the extracted symbols to the symbol list for later usage
                ArrayList<String> entities_list = new ArrayList<>(new HashSet<String>(entities));
                for (String entity : entities_list) {

                    this.symbol_list.add(entity);

                }



//                break kb string into single expression
                ArrayList<String> KB_input_splited = new ArrayList<>(Arrays.asList(KB_input.split(";")));

//                Use reading function to tokenize the expression content and add them to an ArrayList
                for (String input : KB_input_splited) {
                    ArrayList<Node> logic_tree = reading(input);
                    logic_trees.add(logic_tree);
                }
//                  turn all the saving node into trees + save them to an array list
                ArrayList<Node> root_list = new ArrayList<>();
                for (ArrayList<Node> tree : logic_trees) {
                    Node aroot = KB_tree_parse(tree);
                    root_list.add(aroot);

                }

//                Create clauses from the saving trees
                for (Node r : root_list) {
                    if (r.role.equals(Operators.IMPLICATION)) {
                        Clause c = new Clause();
                        c.conclusion = r.right;

                        Queue<Node> q = new LinkedList<Node>();
                        q.add(r.left);
                        while (!q.isEmpty()) {
                            Node check = q.poll();
                            if (check.role.equals(Operators.SENTENCE)) {
                                c.premises.add(check.value);
                            } else {
                                if (!(check.right == null)) {
                                    q.add(check.right);
                                }
                                if (!(check.left == null)) {
                                    q.add(check.left);
                                }
                            }
                        }

                        this.clause_list.add(c);

                    } else {
                        this.truth_list.add(r);
                    }
                }
//                merge all the saving trees into 1 using conjuction nodes
                this.KB_root = CNF_tree_parse(root_list);



            }

//            check the 3rd line of the input file => correct format or not
            String isAsk = myReader.nextLine();
            if (isAsk.trim().equals("ASK")) {
//                read the 4th line => trim it
                String KB_ask = myReader.nextLine().replaceAll(" ", "");

//                split the query (in case there are multiple queries)
                ArrayList<String> KB_ask_splited = new ArrayList<>(Arrays.asList(KB_ask.split(";")));
//                tokenize the query and add them into an arraylist
                ArrayList<ArrayList<Node>> logic_trees_ask = new ArrayList<>();
                for (String input : KB_ask_splited) {
                    ArrayList<Node> logic_tree = reading(input);
                    logic_trees_ask.add(logic_tree);
                }
//                create tree from the tokenized arraylists;
                ArrayList<Node> ask_list = new ArrayList<>();
                for (ArrayList<Node> tree : logic_trees_ask) {
                    Node aroot = KB_tree_parse(tree);
                    ask_list.add(aroot);

                }
//                join them using conjunction
                this.target = CNF_tree_parse(ask_list);
            }


//            close file
            myReader.close();
//          return error if cannot find the file
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();

        }
    }



//    this function is used to tokenize the string input into an arraylist of nodes

    ArrayList<Node> reading(String input) {
//        prepare the output
        ArrayList<Node> logic_tree = new ArrayList<>();
//        loop through the string
        int i = 0;


        while (i < input.length()) {
//            prepare a node to save the symbols
            Node node = new Node();
//            save the symbol's value into node, increment based on the length of the symbol
            switch (input.charAt(i)) {

                case ('('):
                    node.role = Operators.LEFT_BRAC;
                    node.value = "(";
                    logic_tree.add(node);
                    i++;
                    break;
                case (')'):
                    node.role = Operators.RIGHT_BRAC;
                    node.value = ")";
                    logic_tree.add(node);
                    i++;
                    break;
                case ('&'):
                    node.role = Operators.AND;
                    node.value = "&";
                    logic_tree.add(node);
                    i++;
                    break;

                case ('~'):
                    node.role = Operators.NOT;
                    node.value = "~";
                    logic_tree.add(node);
                    i++;
                    break;
                case ('|'):

                    node.role = Operators.OR;
                    node.value = "||";
                    logic_tree.add(node);
                    i++;
                    i++;
                    break;
                case ('='):


                    node.role = Operators.IMPLICATION;
                    node.value = "=>";

                    logic_tree.add(node);
                    i++;
                    i++;
                    break;

                case ('<'):


                    node.role = Operators.BICON;
                    node.value = "<=>";

                    logic_tree.add(node);
                    i++;
                    i++;
                    i++;
                    break;
                default:
//                    dynamically adjust the increment based on the length of the variable
                    if (Character.isLetter(input.charAt(i))) {
                        int endPosition = i + 1;
                        while (endPosition < input.length() && Character.isLetterOrDigit(input.charAt(endPosition))) {
                            endPosition++;
                        }
                        String variable = input.substring(i, endPosition);
                        node.role = Operators.SENTENCE;
                        node.value = variable;
                        logic_tree.add(node);
                        i = endPosition;
                    } else {
                        throw new IllegalArgumentException("Invalid character in expression: " + input.charAt(i));
                    }
                    break;
            }


        }
        return logic_tree;
    }

//    print the tree content, mainly used for debugging
    String tree_print(Node root) {
        String s = root.value;
        if (root.left != null) {
            s = tree_print(root.left) + s;
        }
        if (root.right != null) {
            s = s + tree_print(root.right);
        }
        return s;


    }

//    turn input arraylist of nodes into a tree

    Node KB_tree_parse(ArrayList<Node> tree) {
//        clear the list of saved index of ")"
        this.remove_count.clear();
//        initialize the root node
        Node subroot = new Node();
        subroot.role = Operators.INIT;
//        set the current node is the root node above
        Node current = subroot;
//        loop through the arraylist
        int i = 0;

        while (i < tree.size()) {
//            add value to remove count each iteration
            this.remove_count.add(i);

//            immediately break the iteration if the current value is )
            if (tree.get(i).role.equals(Operators.RIGHT_BRAC)) {

                break;
            }

//            if the current value is a left bracket
            if (tree.get(i).role.equals(Operators.LEFT_BRAC)) {
//                increase the index to the next value of the bracket
                i++;
//                cut the current arraylist from the bracket and then generate a tree from that arraylist (recursively)
                ArrayList<Node> subtree = new ArrayList<>(tree.subList(i, tree.size()));

                Node temp = KB_tree_parse(subtree);

//                replace the original tree with the tree after the recursive
                tree.subList(i - 1, tree.size()).clear();
                tree.addAll(subtree);

//                remove the extra part leftover from the tree forming in the recursive

                tree.subList(i, i + remove_count.get(remove_count.size()-1)).clear();
                tree.set(i - 1, temp);


                if (subroot.role.equals(Operators.INIT)) { //=> if the ( is in the begin of the string
//                    set the current and the root to the root of the content inside the bracket
                    current = temp;
                    subroot = temp;
                } else {
//                    set the right of the current node to tree read from the function
                    current.right = temp;
                    temp.parent = current;
                }


            } else {
//                if the reading is a variable
                if (tree.get(i).role.equals(Operators.SENTENCE)) {
//                    set it as a root if the current node are initialized node
                    if (subroot.role.equals(Operators.INIT)) {
                        current = tree.get(i);
                        subroot = tree.get(i);
                    }
//                    else just ignore variable
                    i++;
//                    if the reading node is a NOT node
                } else if (tree.get(i).role.equals(Operators.NOT)) {
//                    put the next value to the right of the NOT node
                    tree.get(i).right = tree.get(i + 1);
                    tree.get(i+1).parent = tree.get(i);

                    if (subroot.role.equals(Operators.INIT)) {

                        subroot = tree.get(i);
                    }
//                    tree.remove(i + 1);
                    current = tree.get(i);
                    i++;
//                    if not sentence or not => must be normal 2 side operator
                } else {
//                    set the order compare to the current node (current is ensured to be an operator) => to find where exactly to put the current reading operator in
                    Node order_compare = current;
//                    loop through all the operators above to find a solid place to put the current one in (when reading precedence is > the order_compare)
                    while ((tree.get(i).role.ordinal() < order_compare.role.ordinal()) & (order_compare.parent != null)) {
                        order_compare = order_compare.parent;
                    }
//                    if the order compare node does have a parent => push the reading node into the middle of the tree
                    if (order_compare.parent != null) {
                        tree.get(i).left = order_compare.right.parent.right;
                        order_compare.right.parent.right = tree.get(i);
                        tree.get(i).parent = tree.get(i).left.parent;
                        tree.get(i).left.parent = tree.get(i);
                        tree.get(i + 1).parent = tree.get(i);
                        tree.get(i).right = tree.get(i + 1);
                        current = tree.get(i);

//                        if the order compare a single variable  => make the reading into root and the variable parent (this stage is after the initial stage
                    } else if (order_compare.role.equals(Operators.SENTENCE) ) {
                        tree.get(i).left = order_compare;
                        tree.get(i - 1).parent = tree.get(i);
                        tree.get(i + 1).parent = tree.get(i);
                        tree.get(i).right = tree.get(i + 1);
                        subroot = tree.get(i);
                        current = tree.get(i);
                    } else {
//                            if the order compare is an operator and does not have a parent => make the reading node the root

                        tree.get(i).left = order_compare.right.parent;
                        order_compare.right.parent.parent = tree.get(i);
                        tree.get(i).right = tree.get(i + 1);
                        tree.get(i + 1).parent = tree.get(i);
                        subroot = tree.get(i);
                        current = tree.get(i);
                    }
                    i++;
                }
            }
        }

        return subroot;
    }

//    a little misleading name used when i have not fully understand about CNF but still, this function use conjunction to join all the clause into 1
//    => form the knowledge base
    Node CNF_tree_parse(ArrayList<Node> clauses) {
//        if there is only 1 tree => return it
        if (clauses.size() == 1) {
            return clauses.get(0);
        }
//        else use & to connect all clauses into 1
        Node root = new Node();
        root.left = clauses.get(0);
        ArrayList<Node> forward = new ArrayList<>(clauses.subList(1, clauses.size()));
        root.right = CNF_tree_parse(forward);
        root.role = Operators.AND;
        root.value = "&";
        root.right.parent = root;
        root.left.parent = root;
        return root;
    }

//    performing DFS to update the value inside the tree
    void add_value(String variable, boolean value) {

        Stack<Node> stack = new Stack<>();
        stack.push(this.KB_root);
        stack.push(this.target);
        while (!stack.empty()) {
            Node current = stack.pop();
//            if the current search value == the searching value => update its lvalue to the input one
            if (current.value.equals(variable)) {
                current.lvalue = value;
            }
            if (!(current.left == null)) {
                stack.push(current.left);

            }
            if (!(current.right == null)) {
                stack.push(current.right);

            }
        }


    }

//    take in a node => return if the tree is true or false by using recursive
    boolean KB_checking(Node current) {
        if (current.role.equals(Operators.NOT)) {
            return !KB_checking(current.right);
        }
        if (current.role.equals(Operators.AND)) {
            return KB_checking(current.left) & KB_checking(current.right);
        }
        if (current.role.equals(Operators.OR)) {
            return KB_checking(current.left) || KB_checking(current.right);
        }
        if (current.role.equals(Operators.IMPLICATION)) {
            return !KB_checking(current.left) || KB_checking(current.right);
        }
        if (current.role.equals(Operators.BICON)) {
            return (!KB_checking(current.left) || KB_checking(current.right)) & (!KB_checking(current.right) || KB_checking(current.left));
        }
        return current.lvalue;
    }

//    create a fake clone of the KnowledgeBase
    public KnowledgeBase fake_clone() {
        KnowledgeBase clone = new KnowledgeBase(filename);
        return clone;
    }

}
