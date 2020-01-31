package lindenmayer;

public class Symbol {
    public Symbol(){this.sym = null;}
    public Symbol(Character sym){
        this.sym = sym;
    }
    private Character sym;
    public Character getSym() {
        return sym;
    }
    public void setSym(Character sym) {
        this.sym = sym;
    }
    public String toString(){
        return sym.toString();
    }
}
