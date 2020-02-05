package lindenmayer;
import java.awt.*;
import java.awt.geom.Point2D;
import java.io.FileReader;
import java.util.*;
import java.awt.geom.Rectangle2D;
import java.util.List;

import org.json.*;


public class LSystem {

    protected final Random RND;

    private final long init_seed;

    protected LSystem()
    {
        this((new Random()).nextLong());
    }


    protected LSystem(long init_random_seed)
    {
        this.init_seed = init_random_seed;
        this.RND = new Random(init_random_seed);
    }


    protected void resetRandomGenerator()
    {
        this.RND.setSeed(init_seed);
    }

    Rectangle2D.Double biggest_Rectangle;

    HashMap<Character,Symbol> alphabet = new HashMap<>();

    public Symbol addSymbol(char sym) {// Add a symbol to our alphabet (make new symbol and put it in hashmap)
        Symbol symbol = new Symbol(sym);
        alphabet.put(sym, symbol);
        return symbol;
    }
    ////////////////////////Testing Methods/////////////////////////////
    public void printAlphabet(){
        //System.out.println("\nAlphabet : ");
        for(Map.Entry pair: alphabet.entrySet()){
            //System.out.println(alphabet.get(pair.getKey()).getChar());
        }
        //System.out.println("End of alphabet");
    }
    ////////////////////////////////////////////////////////////////////

    HashMap<Symbol,List<Iterator>> rules = new HashMap<>();

    private Iterator<Symbol> getSymbolIterator(String expansion) {
        ArrayList<Symbol> array = new ArrayList();
        for (int i=0;i<expansion.length(); i++) {
            array.add(alphabet.get(expansion.charAt(i)));
        }
        return array.iterator();
    }

    private String getStringFromIterator(Iterator<Symbol> iterator) {
        String s ="";
        while(iterator.hasNext()) {
            s+=iterator.next().toString();
        }
        return s;
    }

    public void addRule(Symbol sym, String expansion) {         // Do we need to check if the rule already exists?
        if(!alphabet.containsValue(sym)){                       // Check if the symbol is in the alphabet
            //System.out.println("\tThe symbol \""+sym+"\" used to make a rule is not in the alphabet");
            return;
        }
        for(int i = 0;i<expansion.length();i++){                // We check if the alphabet contains every character contained in the expansion
            if(alphabet.containsKey(expansion.charAt(i))){
                continue;                                       // If it does, we keep checking for all the other characters
            }                                                   // Else, we don't add the rule because one or more character(s) aren't in our alphabet
            else {
                //System.out.println("\tThe Symbol \""+expansion.charAt(i)+"\" in the expansion of the rule is not in the alphabet");
                return;
            }
        }
        if(rules.containsKey(sym)){                             // If the key is already in the list, we add the expansion to the list of rules of this key
            rules.get(sym).add(getSymbolIterator(expansion));
        }
        else{                                                   // If the key isn't there, we create a new key with a new list
            ArrayList<Iterator> ruleList = new ArrayList<Iterator>();
            ruleList.add(getSymbolIterator(expansion));
            rules.put(sym,ruleList);
        }
    }

    HashMap<Symbol,String> actions = new HashMap<>();
    public void setAction(Symbol sym, String action) {
        actions.put(sym,action);
    }
    Iterator<Symbol> axiom;
    public void setAxiom(String str){
        for(int i = 0;i<str.length();i++){                // We check if the alphabet contains every character contained in the expansion
            if(alphabet.containsKey(str.charAt(i))){
                continue;                                       // If it does, we keep checking for all the other characters
            }                                                   // Else, we don't add the rule because one or more character(s) aren't in our alphabet
            else {
                //System.out.println("\tThe Symbol \""+str.charAt(i)+"\" in the axiom is not in the alphabet");
                return;
            }
        }
        axiom = getSymbolIterator(str);
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
        system.biggest_Rectangle = new Rectangle2D.Double(startX,startY,0,0);

    }

    /* accès aux règles et exécution */
    public Iterator<Symbol> getAxiom(){
        String axiomCopy = getStringFromIterator(axiom);
        setAxiom(axiomCopy);
        return getSymbolIterator(axiomCopy);
    }
    /* opérations avancées */

    public Iterator<Symbol> applyRules(Iterator<Symbol> seq, int n) {
        String seqString = "";
        while(seq.hasNext()) {                      //We copy each symbol in the String "seqString"
            seqString += seq.next().getChar();
        }

        for (int i=0; i<n; i++) {                   // We apply rules n times on the whole axiom string
            seqString = applyRulesOnce(seqString);
            //System.out.println("fin fct "+i+" : "+seqString);
        }

        return getSymbolIterator(seqString);
    }
    private String applyRulesOnce(String seqString) {
        String newAxiom = "";
        for (int i=0; i<seqString.length();i++) {
            //System.out.println("new axiom : " + newAxiom);
            Symbol symbol = alphabet.get(seqString.charAt(i));
            //System.out.println("\t"+rules.get(symbol)+"\t"+symbol.toString());

            if (rules.get(symbol) == null) {
                newAxiom+=seqString.charAt(i);      //We keep the character because it doesn't have any rule
                continue;
            }

            Iterator<Symbol> newSymbols = rewrite(symbol);
            newAxiom += getStringFromIterator(newSymbols);
        }

        return newAxiom;
    }

    public Iterator<Symbol> rewrite(Symbol sym) {
        List<Iterator> sym_rules = rules.get(sym);
        int random = (int)Math.floor(RND.nextDouble()*sym_rules.size());   // We make a random number between 0 and the number of elements-1  and make it an integer
        String ruleData = getStringFromIterator(sym_rules.get(random));    // Since an Iterator can only be iterated through once, we copy it's data and we return a copy of it
        sym_rules.set(random, getSymbolIterator(ruleData));                 // getStringFromIterator iterated through the ruleIterator so we need to reassign a fresh Iterator to the rule

        return getSymbolIterator(ruleData);                               // to select a rule to apply
    }

    public String expansion = "";
    public void tell(Turtle turtle, Symbol sym, int rounds) {
        if (rounds == 0) {                  // If rounds=0 then we are at the end of the recursion and we want tell the turtle to execute the actions
            tell(turtle, sym);
            expansion+=sym.toString();
            return;
        }

        if (rules.get(sym) != null) {       // If the symbol has a rule, we want to call rewrite(). If not, it is a terminal symbol.
            Iterator<Symbol> newSymbols = rewrite(sym);
            while (newSymbols.hasNext()) {
                Symbol s = newSymbols.next();
                tell(turtle, s, rounds-1);
            }
        } else {
            expansion+=sym;
            tell(turtle,sym);
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
        //System.out.println("Pos : "+Math.round(turtle.getPosition().getX() * 100.0) / 100.0+"  "+Math.round(turtle.getPosition().getY() * 100.0) / 100.0+" Angle : "+turtle.getAngle());
        //turtle.stay();
    }


    public Rectangle2D getBoundingBox(Turtle turtle, Iterator<Symbol> seq, int n) {
        // We calculate the maximum and minimum coordinates in X and Y to know which size our canvas has to be
        while(seq.hasNext()){
            Symbol sym = seq.next();
            if(n == 0){
                maxRect(turtle,sym);
            }
            else if (rules.get(sym) != null){
                getBoundingBox(turtle,rewrite(sym),n-1);
            }
            else {
                maxRect(turtle,sym);
            }
        }
        return biggest_Rectangle;

    }

    private void maxRect(Turtle turtle,Symbol sym){
        Point2D.Double old_pos = new Point2D.Double(turtle.getPosition().getX(),turtle.getPosition().getY());      // Union of (the old rectangles obtained so far) with (the new rectangle position)
        tell(turtle,sym);
        Point2D.Double new_pos = new Point2D.Double(turtle.getPosition().getX(),turtle.getPosition().getY());
        double minX = Math.min(old_pos.getX(),new_pos.getX());
        double minY = Math.min(old_pos.getY(),new_pos.getY());
        double maxX = Math.max(old_pos.getX(),new_pos.getX());
        double maxY = Math.max(old_pos.getY(), new_pos.getY());
        ////System.out.println("rectangle avant : "+biggest_Rectangle.toString());
        /*
        //System.out.println("minX : "+minX);
        //System.out.println("minY : "+minY);
        //System.out.println("maxX : "+maxX);
        //System.out.println("maxY : "+maxY);*/
        biggest_Rectangle = (Rectangle2D.Double)biggest_Rectangle.createUnion(new Rectangle2D.Double(minX,minY,maxX-minX,maxY-minY));
        //System.out.println("rectangle apres : "+biggest_Rectangle.toString());

    }
}

















