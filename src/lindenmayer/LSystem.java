package lindenmayer;
import java.awt.geom.Point2D;
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
        System.out.println("\nAlphabet : ");
        for(Map.Entry pair: alphabet.entrySet()){
            System.out.println(alphabet.get(pair.getKey()).getChar());
        }
        System.out.println("End of alphabet");
    }
    public void printRules(){
        System.out.println("\nRules : ");
        for(Map.Entry pair: rules.entrySet()){
            Object key = pair.getKey();
            System.out.println(key.toString());
            for(var rule : rules.get(key) ){// rule is an iterator, we have to change it's type in order to get access to the getCharArray method defined in SymbolIterator
                SymbolIterator symbolIteratorRule = (SymbolIterator)rule;// to then use the toString method which already exists on char []
                char[] charArrayRule = symbolIteratorRule.toCharArray();
                String rule_string = new String(charArrayRule);
                System.out.println("\t"+rule_string);
            }
        }
        System.out.println("End of Rules");
    }
    ////////////////////////////////////////////////////////////////////

    HashMap<Symbol,List<Iterator>> rules = new HashMap<>();

    private class SymbolIterator implements Iterator {
        private Symbol[] symbols;
        private int length = 0;
        private int index = 0;
        public SymbolIterator(String expansion){
            length = expansion.length();
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
            if(index+1 < length) return true;
            else {
                index = 0;
                return false;
            }
        }
        @Override
        public Object next() {
            index++;
            return symbols[index];
        }

        public Symbol current() {
            return symbols[index];
        }

        public char[] toCharArray() {// returns the symbols array as a char array
            char[] c = new char[symbols.length];
            for(int i = 0; i<symbols.length;i++){
                c[i]=symbols[i].getChar();
            }
            return c;
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

        //We store the alphabet
        JSONArray alphabet = input.getJSONArray("alphabet");
        for (int i = 0; i < alphabet.length(); i++) {
            String letter = alphabet.getString(i);
            Symbol sym = system.addSymbol(letter.charAt(0)); // un caractère
        }

        //We store the rules
        JSONObject rules = input.getJSONObject("rules");
        String[] rulesKeys = JSONObject.getNames(rules);
        for(String key : rulesKeys){
            List<Object> values =  rules.getJSONArray(key).toList();
            for(Object value : values){
                system.addRule(system.alphabet.get(key.charAt(0)),value.toString());
            }
        }

        //We store the initial axiom
        String axiom = input.getString("axiom");
        system.setAxiom(axiom);

        //We store the actions
        JSONObject actions = input.getJSONObject("actions");
        String[] actionsKeys = JSONObject.getNames(actions);
        for(String key : actionsKeys){
            system.setAction(system.alphabet.get(key.charAt(0)),actions.get(key).toString());
        }

        //We store the parameters
        JSONObject parameters = input.getJSONObject("parameters");

        double step = Double.parseDouble(parameters.get("step").toString());
        double angleDelta = Double.parseDouble(parameters.get("angle").toString());
        turtle.setUnits(step,angleDelta);

        JSONArray start = parameters.getJSONArray("start");
        double startX = Double.parseDouble(start.get(0).toString());
        double startY = Double.parseDouble(start.get(1).toString());
        double startAngle = Double.parseDouble(start.get(2).toString());
        turtle.init(new Point2D.Double(startX,startY),startAngle);

        SymbolIterator s = (SymbolIterator) system.applyRules(system.getAxiom(),5);
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

    /* opérations avancées */
    public Iterator applyRules(Iterator seq, int n) {

        //We store the Symbols of seq in a new ArrayList "sequence"
        ArrayList<Symbol> seqList = new ArrayList<>();
        SymbolIterator sequence = (SymbolIterator) seq;

        String seqString = "";
        while(sequence.hasNext()) {                 //We copy each symbol in the ArrayList "sequence"
            seqString += sequence.current().getChar();
            sequence.next();
        }
        seqString += sequence.current().getChar();            // Copying the last element

        for (int i=0; i<n; i++) {                   // We apply rules n times on the whole axiom string
            seqString = applyRulesOnce(seqString);
            System.out.println("fin fct "+i+" : "+seqString);
        }

        return new SymbolIterator(seqString);
    }
    private String applyRulesOnce(String seqString) {
        String newAxiom = "";
        for (int i=0; i<seqString.length();i++) {
            Symbol symbol = alphabet.get(seqString.charAt(i));

            if (rules.get(symbol) == null) {
                newAxiom+=seqString.charAt(i);      //We keep the character because it doesn't have any rule
                continue;
            }

            SymbolIterator newSymbols = (SymbolIterator) rewrite(symbol);
            char[] newSymbols2 = newSymbols.toCharArray();
            newAxiom += new String(newSymbols2);
        }

        return newAxiom;
    }

    public void tell(Turtle turtle, Symbol sym, int rounds){
        String expansion = sym.getChar().toString();
        if(rounds == 0)                                     // If we don't want to expand the string, execute the only move provided (1 symbol)
                tell(turtle,sym);
        else                                                // else expand the string 'rounds' times and then execute all the actions
            tell(turtle,sym,expansion,rounds);
    }

    public void tell(Turtle turtle, Symbol sym, String expansion, int rounds){// recursively expand the string 'rounds' times
        if(rounds == 0){                                                      // if we've expanded the string n times, execute all the actions
            //double[] bounds = calculateCanvasBounds(expansion,turtle);

            //getBoundingBox(turtle,new SymbolIterator(expansion),)

            // Now that we know the size of the canvas, we can draw the turtle
            for(int i = 0; i<expansion.length();i++){
                System.out.println(alphabet.get(expansion.charAt(i)));
                tell(turtle,alphabet.get(expansion.charAt(i)) );
            }
        }
        else{
            String newExpansion = applyRulesOnce(expansion);
            tell(turtle,sym,newExpansion,rounds-1);
        }
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
        System.out.println("Pos : "+Math.round(turtle.getPosition().getX() * 100.0) / 100.0+"  "+Math.round(turtle.getPosition().getY() * 100.0) / 100.0+" Angle : "+turtle.getAngle());
        turtle.stay();
    }

    /*
    public Rectangle2D getBoundingBox(Turtle turtle, Iterator seq, int n) {
        // We calculate the maximum and minimum coordinates in X and Y to know which size our canvas has to be
        double minX = 0;
        double minY = 0;
        double maxX = 0;
        double maxY = 0;
        for(int i = 0; i<expansion.length();i++){
            tell(turtle,alphabet.get(expansion.charAt(i)) );
            Point2D pos = turtle.getPosition();
            if(pos.getX()>maxX)
                maxX = pos.getX();
            else if(pos.getX()<minX)
                minX=pos.getX();
            if(pos.getY()>maxY)
                maxY = pos.getY();
            else if(pos.getY()<minY)
                minY = pos.getY();
        }
        return new double[] {minX,minY,maxX,maxY};
    }
    */
}

















