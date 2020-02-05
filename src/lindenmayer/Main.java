package lindenmayer;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class Main {
    public static void main(String[] args) {
        LSystem l = new LSystem();
        MyTurtle t = new MyTurtle();

        try{
            LSystem.readJSONFile("C:\\Users\\Mokova\\Documents\\IFT_2015\\IFT2015-Devoir1\\init.JSON",l,t);
        }
        catch(Exception e){
            System.out.println(e);
        }

        l.printAlphabet();
        System.out.println(l.actions.toString());
        System.out.println(l.rules.toString() );

        l.tell(t,l.getAxiom().next(),2 );
        l.resetRandomGenerator();
        l.getBoundingBox(t,l.getAxiom(),2);
        System.out.println(l.biggest_Rectangle.toString());
        System.out.println(l.expansion);


    }

}
