package ADJAVACALC;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ControllerCalculator implements ActionListener {

    ModelsCalaculator mc;
    ViewsCalculator vc;

    public ControllerCalculator(ModelsCalaculator mc, ViewsCalculator vc) {

        this.mc = mc;
        this.vc = vc;

        vc.add.addActionListener(this);
        vc.sub.addActionListener(this);
        vc.mul.addActionListener(this);
        vc.div.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        double a = Double.parseDouble(vc.txta.getText());
        double b = Double.parseDouble(vc.txtb.getText());

        double result = 0;

        if (e.getSource() == vc.add) {
            result = mc.add(a, b);
        }
        else if (e.getSource() == vc.sub) {
            result = mc.sub(a, b);
        }
        else if (e.getSource() == vc.mul) {
            result = mc.mul(a, b);
        }
        else if (e.getSource() == vc.div) {
            result = mc.div(a, b);
        }

        vc.txtresult.setText(String.valueOf(result));
    }
}