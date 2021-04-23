import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class Application {

    C45Tree tree;

    /*public Application() {

    }*/

    public void start() {
        Attribute age = new Attribute("age");
        Attribute anaemia = new Attribute(2, new String[]{"non_anemic", "anemic"}, "anaemia");
        Attribute creatinine = new Attribute("creatinine");
        Attribute diabetes = new Attribute(2, new String[]{"non_diabetic", "diabetic"}, "diabetes");
        Attribute ejectionFraction = new Attribute("ejectionFraction");
        Attribute highBloodPressure = new Attribute(2, new String[]{"non_hypertensive", "hypertensive"}, "highBloodPressure");
        Attribute platelets = new Attribute("platelets");
        Attribute serumCreatinine = new Attribute("serumCreatinine");
        Attribute serumSodium = new Attribute("serumSodium");
        Attribute sex = new Attribute(2, new String[]{"man", "woman"}, "sex");
        Attribute smoking = new Attribute(2, new String[]{"non_smoking", "smoking"}, "smoking");
        Attribute time = new Attribute("time");
        Attribute deathEvent = new Attribute(2, new String[]{"alive", "dead"}, "deathEvent");
        String dataFile = "heart_failure_clinical_records_dataset_upr.csv";//"heart_failure_clinical_records_dataset_upr.csv";
        Attribute attrs[] = new Attribute[]{age, anaemia, creatinine, diabetes, ejectionFraction, highBloodPressure, platelets,
                serumCreatinine, serumSodium, sex, smoking, time, deathEvent};
        C45Tree c45Tree = new C45Tree(dataFile, attrs, 299, true,true
                , 10);
        tree = c45Tree;
        try {
            c45Tree.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        JTextField numFields[] = new JTextField[7];

        JFrame frm = new JFrame("Heart disease prediction");
        frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frm.setSize(1100,500);

        JPanel panel = new JPanel();
        JLabel ageL = new JLabel("Enter age:");
        JTextField ageT = new JTextField(10);
        panel.add(ageL);
        panel.add(ageT);
        numFields[0] = ageT;
        JLabel anaemiaL = new JLabel("Choose anaemia:");
        JComboBox anaemiaC = new JComboBox(new String[]{"non_anemic", "anemic"});
        panel.add(anaemiaL);
        panel.add(anaemiaC);
        JLabel creatL = new JLabel("Enter creatinine:");
        JTextField creatT = new JTextField(10);
        panel.add(creatL);
        panel.add(creatT);
        numFields[1] = creatT;
        JLabel diabetesL = new JLabel("Choose diabetes:");
        JComboBox diabetesC = new JComboBox(new String[]{"non_diabetic", "diabetic"});
        panel.add(diabetesL);
        panel.add(diabetesC);
        JLabel ejFracL = new JLabel("Enter ejection fraction:");
        JTextField ejFracT = new JTextField(10);
        panel.add(ejFracL);
        panel.add(ejFracT);
        numFields[2] = ejFracT;
        JLabel hBlPrL = new JLabel("Choose blood pressure:");
        JComboBox hBlPrC = new JComboBox(new String[]{"non_hypertensive", "hypertensive"});
        panel.add(hBlPrL);
        panel.add(hBlPrC);
        JLabel platelL = new JLabel("Enter platelets:");
        JTextField platelT = new JTextField(10);
        panel.add(platelL);
        panel.add(platelT);
        numFields[3] = platelT;
        JLabel serCrL = new JLabel("Enter serum creatinine:");
        JTextField serCrT = new JTextField(10);
        panel.add(serCrL);
        panel.add(serCrT);
        numFields[4] = serCrT;
        JLabel serSdL = new JLabel("Enter serum sodium:");
        JTextField serSdT = new JTextField(10);
        panel.add(serSdL);
        panel.add(serSdT);
        numFields[5] = serSdT;
        JLabel sexL = new JLabel("Choose sex:");
        JComboBox sexC = new JComboBox(new String[]{"man", "woman"});
        panel.add(sexL);
        panel.add(sexC);
        JLabel smokL = new JLabel("Choose smoking:");
        JComboBox smokC = new JComboBox(new String[]{"non_smoking", "smoking"});
        panel.add(smokL);
        panel.add(smokC);
        JLabel timeL = new JLabel("Enter time:");
        JTextField timeT = new JTextField(10);
        panel.add(timeL);
        panel.add(timeT);
        numFields[6] = timeT;
        JButton but = new JButton("Results");
        panel.add(but);
        JLabel message = new JLabel("");
        panel.add(message);
        frm.getContentPane().add(panel);
        //frm.getContentPane().add(BorderLayout.CENTER, but);
        System.out.println(anaemiaC.getSelectedIndex());
        double numFieldsParsed[] = new double[7];
        ActionListener actionListener = new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                for(int i = 0; i < numFields.length; i++) {
                    try{
                        numFieldsParsed[i] = Double.parseDouble(numFields[i].getText());
                    } catch (NumberFormatException e) {
                        message.setText("Wrong number input!");
                        return;
                    }
                }
                Sample samp = new Sample(new AttValue[]{new AttValue(true, "", numFieldsParsed[0],attrs[0].getName()),
                        new AttValue(false, anaemiaC.getSelectedItem().toString(), 0, attrs[1].getName()),
                        new AttValue(true, "", numFieldsParsed[1],attrs[2].getName()),
                        new AttValue(false, diabetesC.getSelectedItem().toString(), 0, attrs[3].getName()),
                        new AttValue(true, "", numFieldsParsed[2],attrs[4].getName()),
                        new AttValue(false, hBlPrC.getSelectedItem().toString(), 0, attrs[5].getName()),
                        new AttValue(true, "", numFieldsParsed[3],attrs[6].getName()),
                        new AttValue(true, "", numFieldsParsed[4],attrs[7].getName()),
                        new AttValue(true, "", numFieldsParsed[5],attrs[8].getName()),
                        new AttValue(false, sexC.getSelectedItem().toString(), 0, attrs[9].getName()),
                        new AttValue(false, smokC.getSelectedItem().toString(), 0, attrs[10].getName()),
                        new AttValue(true, "", numFieldsParsed[6],attrs[11].getName()),
                        new AttValue(false, "unknown", 0, attrs[12].getName())
                });
                int indexClass = c45Tree.testSample(samp);
                int numOfClass = attrs[attrs.length-1].getNumberOfValues();
                if(indexClass == 0) {
                    message.setText("Risk wasnt found");
                } else{
                    message.setText("Patient should stay in hospital, there may be risk of heart failure");
                }
            }
        };
        but.addActionListener(actionListener);

        frm.setVisible(true);

        //Sample samp = new Sample(new AttValue[]{new AttValue(true, "", Double.parseDouble(ageT.getText()),attrs[0].getName())});
    }
}
