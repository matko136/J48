import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        Attribute age = new Attribute();
        Attribute anaemia = new Attribute(2, new String[]{"non_anemic", "anemic"});
        Attribute creatinine = new Attribute();
        Attribute diabetes = new Attribute(2, new String[]{"non_diabetic", "diabetic"});
        Attribute ejectionFraction = new Attribute();
        Attribute highBloodPressure = new Attribute(2, new String[]{"non_hypertensive", "hypertensive"});
        Attribute platelets = new Attribute();
        Attribute serumCreatinine = new Attribute();
        Attribute serumSodium = new Attribute();
        Attribute sex = new Attribute(2, new String[]{"man", "woman"});
        Attribute smoking = new Attribute(2, new String[]{"non_smoking", "smoking"});
        Attribute time = new Attribute();
        Attribute deathEvent = new Attribute(2, new String[]{"alive", "dead"});

        String dataFile = "heart_failure_clinical_records_dataset_upr.csv";
        C45Tree c45Tree = new C45Tree(dataFile, new Attribute[]{age, anaemia, creatinine, diabetes, ejectionFraction, highBloodPressure, platelets,
        serumCreatinine, serumSodium, sex, smoking, time, deathEvent}, 299);
        try {
            c45Tree.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
