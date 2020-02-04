package lindenmayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class Main {
    public static void main(String[] args) {
        LSystem l = new LSystem();
        MyTurtle t = new MyTurtle();

        try{
            LSystem.readJSONFile("C:\\Users\\Hugo\\Desktop\\Cours Informatique\\IFT-2015\\IFT2015-Devoir1\\init.JSON",l,t);
        }
        catch(Exception e){
            System.out.println(e);
        }

        l.printAlphabet();
        System.out.println(l.actions.toString());
        System.out.println(l.rules.toString() );

        l.tell(t,l.alphabet.get('F'),4 );
        System.out.println(l.test);
    }
}
