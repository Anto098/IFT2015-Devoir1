package lindenmayer;
import java.io.FileReader;
import java.util.*;
import java.awt.geom.Rectangle2D;
import org.json.*;

public class LSystem {
    /**
     * constructeur vide monte un système avec alphabet vide et sans règles
     */
    public LSystem(){ }
    /* méthodes d'initialisation de système */

    HashMap<Character,Symbol> alphabet = new HashMap<>();

    public Symbol addSymbol(char sym) {// Add a symbol to our alphabet (make new symbol and put it in hashmap)
        Symbol symbol = new Symbol(sym);
        alphabet.put(sym, symbol);
        return symbol;
    }
    ////////////////////////Testing Methods/////////////////////////////
    public void printAlphabet(){
        System.out.println("Alphabet : ");
        for(Map.Entry pair: alphabet.entrySet()){
            System.out.println(alphabet.get(pair.getKey()).getSym());
        }
        System.out.println("End of alphabet");
    }
    /*public void printRules(){
        System.out.println("Rules : ");
        for(Map.Entry pair: rules.entrySet()){
            System.out.println(pair.getKey().toString() + " : " + alphabet.get(pair.getKey()) );
        }
        System.out.println("End of Rules");
    }*/
    ////////////////////////////////////////////////////////////////////

    HashMap<Symbol,List<Iterator>> rules = new HashMap<>();

    private class SymbolIterator implements Iterator {
        private Symbol[] symbols;
        private int index = 0;
        public SymbolIterator(String expansion){
            int length = expansion.length();
            symbols = new Symbol[length];
            for(int i = 0; i<length;i++){
                symbols[i] = alphabet.get(expansion.charAt(i));
            }
        }
        public Symbol[] getsymbols() {
            return symbols;
        }
        @Override
        public boolean hasNext() {
            if(symbols[index+1] != null) return true;
            else{
                index = 0;
                return false;
            }
        }
        @Override
        public Object next() {
            index++;
            return symbols[index];
        }
        public String toString(){
            return Arrays.toString(symbols);
        }
    }
    public void addRule(Symbol sym, String expansion) {         // Do we need to check if the rule already exists?
        if(!alphabet.containsValue(sym)){                       // Check if the symbol is in the alphabet
            System.out.println("\tThe symbol \""+sym+"\" used to make a rule is not in the alphabet");
            return;
        }
        for(int i = 0;i<expansion.length();i++){                // We check if the alphabet contains every character contained in the expansion
            if(alphabet.containsKey(expansion.charAt(i))){
                continue;                                       // If it does, we keep checking for all the other characters
            }                                                   // Else, we don't add the rule because one or more character(s) aren't in our alphabet
            else {
                System.out.println("\tThe Symbol \""+expansion.charAt(i)+"\" in the expansion of the rule is not in the alphabet");
                return;
            }
        }
        if(rules.containsKey(sym)){                             // If the key is already in the list, we add the expansion to the list of rules of this key
            rules.get(sym).add(new SymbolIterator(expansion));
        }
        else{                                                   // If the key isn't there, we create a new key with a new list
            ArrayList<Iterator> ruleList = new ArrayList<Iterator>();
            ruleList.add(new SymbolIterator(expansion));
            rules.put(sym,ruleList);
        }
    }

    HashMap<Symbol,String> actions = new HashMap<>();
    public void setAction(Symbol sym, String action) {
        actions.put(sym,action);
    }
    SymbolIterator axiom;
    public void setAxiom(String str){
        for(int i = 0;i<str.length();i++){                // We check if the alphabet contains every character contained in the expansion
            if(alphabet.containsKey(str.charAt(i))){
                continue;                                       // If it does, we keep checking for all the other characters
            }                                                   // Else, we don't add the rule because one or more character(s) aren't in our alphabet
            else {
                System.out.println("\tThe Symbol \""+str.charAt(i)+"\" in the axiom is not in the alphabet");
                return;
            }
        }
        axiom = new SymbolIterator(str);
    }

    /* initialisation par fichier */
    public static void readJSONFile(String filename, LSystem system, Turtle turtle) throws java.io.IOException {
        JSONObject input = new JSONObject(new JSONTokener(new FileReader(filename))); // lecture de fichier JSON avec JSONTokener
        JSONArray alphabet = input.getJSONArray("alphabet");
        System.out.println(alphabet);

        JSONObject rules = input.getJSONObject("rules");
        System.out.println(rules);

        JSONObject actions = input.getJSONObject("actions");
        System.out.println(actions);

        for (int i = 0; i < alphabet.length(); i++) {
            String letter = alphabet.getString(i);
            Symbol sym = system.addSymbol(letter.charAt(0)); // un caractère
        }
        String axiom = input.getString("axiom");
        system.setAxiom(axiom);

        String[] keys = JSONObject.getNames(rules);
        for(String key : keys){
            List<Object> values =  rules.getJSONArray(key).toList();
            for(Object value : values){
                System.out.println("value is : "+value);
                system.addRule(system.alphabet.get(key.charAt(0)),value.toString());//TODO finir la fonction qui print les rules
            }
        }


    }
    /* accès aux règles et exécution */
    public Iterator getAxiom(){
        return axiom;
    }
    public Iterator rewrite(Symbol sym) {
        List<Iterator> sym_rules = rules.get(sym);
        int random = (int)Math.floor(Math.random()*sym_rules.size());   // We make a random number between 0 and the number of elements-1  and make it an integer
        return sym_rules.get(random);                                   // to select a rule to apply
    }
    public void tell(Turtle turtle, Symbol sym) {
        String action = actions.get(sym);
        switch(action){
            case "draw": turtle.draw();
                break;
            case "move": turtle.move();
                break;
            case "turnR": turtle.turnR();
                break;
            case "turnL": turtle.turnL();
                break;
            case "push": turtle.push();
                break;
            case "pop": turtle.pop();
                break;
            case "stay": turtle.stay();
                break;
        }
    }

    /* opérations avancées */
    //public Iterator applyRules(Iterator seq, int n) {}
    public void tell(Turtle turtle, Symbol sym, int rounds){}
    //public Rectangle2D getBoundingBox(Turtle turtle, Iterator seq, int n) {}
}