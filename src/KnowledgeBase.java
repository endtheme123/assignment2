import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Integer.parseInt;
import static java.util.Arrays.stream;

public class KnowledgeBase {

    ArrayList<String> symbol_list;

    ArrayList<Node> truth_list;

    ArrayList<ArrayList<Node>> logic_trees;
    ArrayList<Clause> clause_list;

    String filename;
    Node target;
    String[] operators;
    Node KB_root;
    ArrayList<Integer> remove_count;

    public KnowledgeBase(String filename) {
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
        try {
//            read the filename
            File myObj = new File(filepath);
//            read the file
            Scanner myReader = new Scanner(myObj);
//            read in the size of the map
            String isTell = myReader.nextLine();
            if (isTell.trim().equals("TELL")) {

                String KB_input = myReader.nextLine().replaceAll(" ", "");

//                get the list of entities and initialize graph
                String KB_input_1 = KB_input;
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
                ArrayList<String> entities_list = new ArrayList<>(new HashSet<String>(entities));
                for (String entity : entities_list) {

                    this.symbol_list.add(entity);

                }

//                create the relationship between vertices

//                break the implication into literals
                ArrayList<String> KB_input_splited = new ArrayList<>(Arrays.asList(KB_input.split(";")));


                for (String input : KB_input_splited) {
                    ArrayList<Node> logic_tree = reading(input);
                    logic_trees.add(logic_tree);
                }

                ArrayList<Node> root_list = new ArrayList<>();
                for (ArrayList<Node> tree : logic_trees) {
                    Node aroot = KB_tree_parse(tree);
                    root_list.add(aroot);

                }


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

                this.KB_root = CNF_tree_parse(root_list);



            }


            String isAsk = myReader.nextLine();
            if (isAsk.trim().equals("ASK")) {
                String KB_ask = myReader.nextLine().replaceAll(" ", "");
                ArrayList<String> KB_ask_splited = new ArrayList<>(Arrays.asList(KB_ask.split(";")));
                ArrayList<ArrayList<Node>> logic_trees_ask = new ArrayList<>();
                for (String input : KB_ask_splited) {
                    ArrayList<Node> logic_tree = reading(input);
                    logic_trees_ask.add(logic_tree);
                }
                ArrayList<Node> ask_list = new ArrayList<>();
                for (ArrayList<Node> tree : logic_trees_ask) {
                    Node aroot = KB_tree_parse(tree);
                    ask_list.add(aroot);

                }
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



    ArrayList<Node> reading(String input) {
        ArrayList<Node> logic_tree = new ArrayList<>();
        int i = 0;


        while (i < input.length()) {
            Node node = new Node();
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


    Node KB_tree_parse(ArrayList<Node> tree) {
        this.remove_count.clear();
        Node subroot = new Node();
        subroot.role = Operators.INIT;
        Node current = subroot;
        int i = 0;
        while (i < tree.size()) {
            this.remove_count.add(i);
            if (tree.get(i).role.equals(Operators.RIGHT_BRAC)) {

                break;
            }


            if (tree.get(i).role.equals(Operators.LEFT_BRAC)) {

                i++;
                ArrayList<Node> subtree = new ArrayList<>(tree.subList(i, tree.size()));

                Node temp = KB_tree_parse(subtree);
                tree.subList(i - 1, tree.size()).clear();
                tree.addAll(subtree);




                tree.subList(i, i + remove_count.get(remove_count.size()-1)).clear();
                tree.set(i - 1, temp);



                if (subroot.role.equals(Operators.INIT)) {

                    current = temp;
                    subroot = temp;
                } else {
                    current.right = temp;
                    temp.parent = current;
                }


            } else {

                if (tree.get(i).role.equals(Operators.SENTENCE)) {

                    if (subroot.role.equals(Operators.INIT)) {
                        current = tree.get(i);
                        subroot = tree.get(i);
                    }

                    i++;

                } else if (tree.get(i).role.equals(Operators.NOT)) {
                    tree.get(i).right = tree.get(i + 1);

                    if (subroot.role.equals(Operators.INIT)) {

                        subroot = tree.get(i);
                    }
//                    tree.remove(i + 1);
                    current = tree.get(i);
                    i++;
                } else {
                    Node order_compare = current;
                    while ((tree.get(i).role.ordinal() < order_compare.role.ordinal()) & (order_compare.parent != null)) {
                        order_compare = order_compare.parent;
                    }

                    if (order_compare.parent != null) {
                        tree.get(i).left = order_compare.right.parent.right;
                        order_compare.right.parent.right = tree.get(i);
                        tree.get(i).parent = tree.get(i).left.parent;
                        tree.get(i).left.parent = tree.get(i);
                        tree.get(i + 1).parent = tree.get(i);
                        tree.get(i).right = tree.get(i + 1);
                        current = tree.get(i);


                    } else if (order_compare.role.equals(Operators.SENTENCE) || order_compare.role.equals(Operators.NOT)) {
                        tree.get(i).left = order_compare;
                        tree.get(i - 1).parent = tree.get(i);
                        tree.get(i + 1).parent = tree.get(i);
                        tree.get(i).right = tree.get(i + 1);
                        subroot = tree.get(i);
                        current = tree.get(i);
                    } else {


                        tree.get(i).left = order_compare.left.parent;
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

    Node CNF_tree_parse(ArrayList<Node> clauses) {
        if (clauses.size() == 1) {
            return clauses.get(0);
        }
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

    void add_value(String variable, boolean value) {

        Stack<Node> stack = new Stack<>();
        stack.push(this.KB_root);
        stack.push(this.target);
        while (!stack.empty()) {
            Node current = stack.pop();
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


    public KnowledgeBase fake_clone() {
        KnowledgeBase clone = new KnowledgeBase(filename);
        return clone;
    }

}
