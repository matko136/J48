import java.io.IOException;

public class Main {
    public static void main(String[] args) {
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

        String dataFile = "heart_failure_clinical_records_dataset_upr.csv";
        C45Tree c45Tree = new C45Tree(dataFile, new Attribute[]{age, anaemia, creatinine, diabetes, ejectionFraction, highBloodPressure, platelets,
        serumCreatinine, serumSodium, sex, smoking, time, deathEvent}, 299, false, 10);
        try {
            c45Tree.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        c45Tree.drawTree();

    }
}
