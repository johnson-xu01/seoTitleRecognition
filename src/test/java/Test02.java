import anti_seo.Anti_SEO_Solver;

public class Test02 {

    public static void main(String[] args) throws Exception {
        Anti_SEO_Solver antiSEO = Anti_SEO_Solver.getInstance();
        antiSEO.connect();//加载model
        antiSEO.solveSingleFile_LR_CM(
                "D:\\IntelliJ IDEA 2017.2.2\\workspace\\seoTitleRecognition\\titles_test.txt",
                "D:\\IntelliJ IDEA 2017.2.2\\workspace\\seoTitleRecognition\\titles_test_1.txt",
                "D:\\IntelliJ IDEA 2017.2.2\\workspace\\seoTitleRecognition\\titles_test_0.txt");

//        float[] res = antiSEO.getWordVector("单团");
//        String line="";
//        for (Float f : res) {
//            line += f + " ";
//        }
//        System.out.println(line);
//        System.out.println(antiSEO.wordSimilarity("java","java开发"));
//        int predict = antiSEO.predict("物业保安班长/保安队长/安保主管 ");
//        System.out.println(predict);
        System.out.println("end");
    }

}