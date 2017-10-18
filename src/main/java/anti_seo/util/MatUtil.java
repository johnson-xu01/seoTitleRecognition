package anti_seo.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class MatUtil {
    /**
     * 计算向量内积
     *
     * @param vec1
     * @param vec2
     * @return
     */
    private static float dot(float[] vec1, float[] vec2) {
        if (vec1.length != vec2.length) {
            return 0;
        }
        float dist = 0;
        for (int i = 0; i < vec1.length; i++) {
            dist += vec1[i] * vec2[i];
        }
        return dist;
    }

    /**
     * 计算两个向量的余弦夹角
     *
     * @param vec1
     * @param vec2
     * @return
     */
    public static float cosDist(float[] vec1, float[] vec2) {
        float dist = 0;
        float vec1_norm = 0;
        float vec2_norm = 0;
        for (int i = 0; i < vec1.length; i++) {
            dist += vec1[i] * vec2[i];
            vec1_norm += vec1[i] * vec1[i];
            vec2_norm += vec2[i] * vec2[i];
        }
        vec1_norm = (float) Math.sqrt(vec1_norm);
        vec2_norm = (float) Math.sqrt(vec2_norm);
        return dist / (vec1_norm * vec2_norm);
    }

    /**
     * 两个向量加法（element-wise）
     *
     * @param vec1
     * @param vec2
     * @return
     */
    public static float[] add(float[] vec1, float[] vec2) {
        float[] add_res = new float[vec1.length];
        for (int i = 0; i < vec1.length; i++) {
            add_res[i] = vec1[i] + vec2[i];
        }
        return add_res;
    }

    public static float sigmoid(float x) {
        return (float) (1 / (1 + Math.exp(-x)));
    }

    public static int predict(float[] vec1, float[] vec2) {
        float prediction = sigmoid(dot(vec1, vec2));
        return prediction > 0.5 ? 1 : 0;
    }

    public float[] loadLRmodel() {
        String modelPath = "/LR_model.m";
        InputStream is = null;
        BufferedReader br = null;
        int embbed_size = 150;
        float[] res = new float[embbed_size];
        try {
            is = this.getClass().getResourceAsStream(modelPath);
            br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            for (int i = 0; i < embbed_size; i++) {
                String line = br.readLine().trim();
                res[i] = Float.valueOf(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (br != null) {
                    br.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return res;
    }

    Map<String, Integer> positions_labelled_dict;

    public void loadPositionLabelled() {
        long s = System.currentTimeMillis();
        String modelPath = "/positions_labelled_115_0928.txt";
        positions_labelled_dict = new HashMap<String, Integer>();
        InputStream is = null;
        BufferedReader br = null;
        try {
            is = this.getClass().getResourceAsStream(modelPath);
            br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            for (int i = 0; i < 2040; i++) {
                String line = br.readLine().trim();
                String[] split = line.split(" ");
                positions_labelled_dict.put(split[0], Integer.valueOf(split[1]));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (br != null) {
                    br.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        long e = System.currentTimeMillis();
        System.out.println("Logistic regression model load finished, time elapsed " + (e - s) + " ms.");
    }

    public int getScore(List<String> sen_words) {
        HashSet<Integer> tmp = new HashSet<>();
        for (String word : sen_words) {
            if (positions_labelled_dict.containsKey(word)) {
                tmp.add(positions_labelled_dict.get(word));
            }
        }
        return tmp.size();
    }

    public static void main(String[] args) {
//        MatUtil matUtil = new MatUtil();
//        float[] weight = matUtil.loadLRmodel();
//        float res = sigmoid(100);
//        float[] vec1 = {0.1f,-0.2f,0.3f};
//        float[] vec2 = {0.4f,0.5f,-0.6f};
//        float sigmoid = sigmoid(dot(vec1, vec2));
//        System.out.println(sigmoid);
//        System.out.println(predict(vec1, vec2));
        MatUtil matUtil = new MatUtil();
        matUtil.loadPositionLabelled();
        List<String> sen_words = new ArrayList<>();

        sen_words.add("招商部经理");
        sen_words.add("分销业务员");
        sen_words.add("私人秘书");
        sen_words.add("测量员");
        sen_words.add("产品运营");
        sen_words.add("站务员");
        sen_words.add("资本运作");
        int score = matUtil.getScore(sen_words);
        System.out.println(score);
    }

}
