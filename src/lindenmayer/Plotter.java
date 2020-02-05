/*
 * Copyright 2020 Mikl&oacute;s Cs&#369;r&ouml;s.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package lindenmayer;


import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.FileReader;
import java.io.PrintStream;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 *
 * Entry point for drawing L-System in PostScript.
 * Call with command-line arguments: JSON file specifying and number of iterations: 
 * <code>java ... Plotter [-o output.ps] lsystem.json n</code> 
 *
 * @author Mikl&oacute;s Cs&#369;r&ouml;s
 */
public class Plotter
{
    private PrintStream out = System.out;

    Plotter(LSystem ls, EPSTurtle turtle)
    {
        this.lsystem = ls;
        this.turtle = turtle;
    }
    private LSystem lsystem;
    private EPSTurtle turtle;

    private Plotter()
    {
        this(new LSystem(), new EPSTurtle(new MyTurtle()));
    }

    /**
     * Parses the JSON specification of an L-System. 
     *
     * @param params
     * @throws Exception if I/O problems or bad JSON
     */
    private void parseLSystem(JSONObject input) throws Exception
    {
        //We store the alphabet
        JSONArray alphabet = input.getJSONArray("alphabet");
        for (int i = 0; i < alphabet.length(); i++) {
            String letter = alphabet.getString(i);
            Symbol sym = lsystem.addSymbol(letter.charAt(0)); // un caractère
        }

        //We store the rules
        JSONObject rules = input.getJSONObject("rules");
        String[] rulesKeys = JSONObject.getNames(rules);
        for(String key : rulesKeys){
            List<Object> values =  rules.getJSONArray(key).toList();
            for(Object value : values){
                lsystem.addRule(lsystem.alphabet.get(key.charAt(0)),value.toString());
            }
        }

        //We store the initial axiom
        String axiom = input.getString("axiom");
        lsystem.setAxiom(axiom);

        //We store the actions
        JSONObject actions = input.getJSONObject("actions");
        String[] actionsKeys = JSONObject.getNames(actions);
        for(String key : actionsKeys){
            lsystem.setAction(lsystem.alphabet.get(key.charAt(0)),actions.get(key).toString());
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
        lsystem.biggest_Rectangle = new Rectangle2D.Double(startX,startY,0,0);
    }

    /**
     * Instance-linked main. 
     *
     * @param args
     * @throws Exception
     */
    private void allezallez(String[] args) throws Exception
    {
        int arg_idx=0;
        while (arg_idx<args.length && args[arg_idx].startsWith("-"))
        {
            String arg_key = args[arg_idx++];
            if (arg_idx==args.length)
                throw new IllegalArgumentException("Missing value for option "+arg_key);
            if ("-o".equals(arg_key))
            {
                String output_file = args[arg_idx++];
                out = "=".equals(output_file)?System.out:new java.io.PrintStream(output_file);
            } else
            {
                throw new IllegalArgumentException("Switch "+arg_key+" not recognized (-o output.ps)");
            }
        }

        if (arg_idx==args.length)
            throw new IllegalArgumentException("Give JSON file name as command-line argument: java ... "+getClass().getName()+" lsystem.json niter");
        String json_file = args[arg_idx++];
        if (arg_idx==args.length)
            throw new IllegalArgumentException("Give number of rewriting iterations as the last command-line argument: java ... "+getClass().getName()+" lsystem.json niter");
        int n_iter = Integer.parseInt(args[arg_idx++]);

        this.lsystem = new LSystem();
        this.turtle = new EPSTurtle(new MyTurtle(), out);

        JSONObject params = new JSONObject(new JSONTokener(new java.io.FileReader(json_file)));
        parseLSystem(params);

        turtle.plot(lsystem, n_iter);
    }

    public static void main(String[] args) throws Exception
    {
        Plotter P = new Plotter();
        P.allezallez(args);
    }


}