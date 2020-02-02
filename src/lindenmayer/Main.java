package lindenmayer;

public class Main {
    public static void main(String[] args) {
        LSystem l = new LSystem();
        MyTurtle t = new MyTurtle();
        /*
        l.addSymbol('F');
        l.addSymbol('A');
        System.out.println(l.alphabet.toString());
        l.printAlphabet();

        l.addRule(l.alphabet.get('A'),"AF");
        l.addRule(l.alphabet.get('A'),"AFFFF");
        l.addRule(l.alphabet.get('F'),"AFAFAFA");
        l.addRule(l.alphabet.get('F'),"e");

        System.out.println(l.rules.toString() );
         */
        try{
            LSystem.readJSONFile("C:\\Users\\Hugo\\Desktop\\Cours Informatique\\IFT-2015\\IFT2015-Devoir1\\init.JSON",l,t);
        }
        catch(Exception e){
            System.out.println(e);
        }
        l.printAlphabet();
        l.printRules();
        System.out.println(l.actions.toString());

        //System.out.println(l.rules.toString() );
    }
}
