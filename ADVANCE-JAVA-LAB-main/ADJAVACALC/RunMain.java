package ADJAVACALC;

public class RunMain {

    public static void main(String[] args) {

        ModelsCalaculator mc = new ModelsCalaculator();
        ViewsCalculator vc = new ViewsCalculator();

        ControllerCalculator cc = new ControllerCalculator(mc, vc);

    }
}