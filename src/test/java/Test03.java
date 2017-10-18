public class Test03 {
    public static void add(float[] vec1, float[] vec2){
        for (int i = 0; i < vec1.length; i++) {
            vec1[i] += vec2[i];
        }
    }

    public static void main(String[] args) {
        float[] vec1 = {1, 2, 3};
        float[] vec2 = {4, 5, 6};
        add(vec1, vec2);
        System.out.println();
    }
}
