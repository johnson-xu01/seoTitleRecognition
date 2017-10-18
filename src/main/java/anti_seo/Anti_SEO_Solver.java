package anti_seo;

import com.huaban.analysis.jieba.JiebaSegmenter;

import java.io.*;
import java.util.*;

public class Anti_SEO_Solver {
    private HashMap<String, float[]> word2vec = new HashMap<String, float[]>();
    private int Wcount;
    private int embbed_size = 150;
    private boolean loadModelTF = false; //是否已经加载模型标志
    private JiebaSegmenter segmenter;//分词器
    private float[] weight; // LR model 权重
    private float bias = -16.1940830414f;
    private Map<String, Integer> positions_labelled_dict;
    private static final Anti_SEO_Solver single = new Anti_SEO_Solver();

    public static Anti_SEO_Solver getInstance() {
        return single;
    }

    public void connect() {
        segmenter = new JiebaSegmenter();
        loadWord2vec();
        loadLogisticRegression();
        loadPositionLabelled();
        loadModelTF = true;
    }

    /////////////////////////////////////////////////////////////////////////
    ////word2vec model相关操作
    /////////////////////////////////////////////////////////////////////////

    /**
     * 加载Word2Vec模型
     */
    private void loadWord2vec() {
        String modelPath = "/job_title_word2vec_google_format.model";
        long s = System.currentTimeMillis();
        this.loadGoogleModel(modelPath);
        long e = System.currentTimeMillis();
        System.out.println("word2vec model load finished, time elapsed " + (e - s) + " ms.");
    }

    private void loadGoogleModel(String modelPath) {
        InputStream is = null;
        BufferedReader br = null;
        try {
            is = this.getClass().getResourceAsStream(modelPath);
            br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String firstLine = br.readLine().trim();
            String[] sp = firstLine.split(" ");
            this.Wcount = Integer.parseInt(sp[0]); // 读取词数
            this.embbed_size = Integer.parseInt(sp[1]);// 大小
            String word;
            float[] word_embbed = null;
            for (int i = 0; i < this.Wcount; i++) {
                String line = br.readLine().trim();
                String[] split = line.split(" ");
                word = split[0].toLowerCase();
                if (split.length < 151) {
                    System.out.println("line " + (i + 1) + ": " + word);
                    continue;
                }
                word_embbed = new float[this.embbed_size];
                for (int j = 0; j < this.embbed_size; j++) {
                    word_embbed[j] = Float.valueOf(split[j + 1]);
                }
                word2vec.put(word, word_embbed);
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
    }

    public boolean containWord(String word) {
        if (!loadModelTF) return false;
        return word2vec.containsKey(word);
    }

    /**
     * 获得词向量
     *
     * @param word
     * @return
     */
    public float[] getWordVector(String word) {
        if (!loadModelTF) {
            return null;
        }
        return word2vec.get(word);
    }

    /**
     * 计算向量内积
     *
     * @param vec1
     * @param vec2
     * @return
     */
    private float calDist(float[] vec1, float[] vec2) {
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
     * 计算词相似度
     *
     * @param word1
     * @param word2
     * @return
     */
    public float wordSimilarity(String word1, String word2) {
        if (loadModelTF == false) {
            return -1;
        }
        float[] word1Vec = getWordVector(word1);
        float[] word2Vec = getWordVector(word2);
        if (word1Vec == null || word2Vec == null) {
            return -1;
        }
        return calDist(word1Vec, word2Vec);
    }

    /**
     * 两个向量加法（element-wise）
     *
     * @param vec1
     * @param vec2
     */
    private void add(float[] vec1, float[] vec2) {
        for (int i = 0; i < vec1.length; i++) {
            vec1[i] += vec2[i];
        }
    }
    /////////////////////////////////////////////////////////////////////////

    /////////////////////////////////////////////////////////////////////////
    ////LR model相关操作
    /////////////////////////////////////////////////////////////////////////

    /**
     * 对句子进行分词函数
     *
     * @param sen: 待分词句子
     * @return : 词序列
     */
    public List<String> seg_sentence(String sen) {
        return this.segmenter.sentenceProcess(sen);
    }

    /**
     * sentence2vec函数
     *
     * @param sen
     * @return
     */
    public float[] sentence2vec(String sen) {
        List<String> sen_words = this.seg_sentence(sen);
        float[] sen_vec = new float[this.embbed_size];
        for (String word : sen_words) {
            if (this.containWord(word)) {
                float[] word_vec = this.getWordVector(word);
                this.add(sen_vec, word_vec);
            }
        }
        return sen_vec;
    }

    /**
     * 加载LR model
     */
    private void loadLogisticRegression() {
        long s = System.currentTimeMillis();
        String modelPath = "/LR_model.m";
        InputStream is = null;
        BufferedReader br = null;
        weight = new float[embbed_size];
        try {
            is = this.getClass().getResourceAsStream(modelPath);
            br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            for (int i = 0; i < embbed_size; i++) {
                String line = br.readLine().trim();
                weight[i] = Float.valueOf(line);
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

    private float sigmoid(float x) {
        return (float) (1 / (1 + Math.exp(-x)));
    }

    private float dot(float[] vec1, float[] vec2) {
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
     * 逻辑回归预测
     *
     * @param sen
     * @return
     */
    public int predict(String sen) {
        float[] sen_vec = this.sentence2vec(sen);
        float prediction = this.dot(sen_vec, weight) + bias;
        return prediction > 0 ? 1 : 0;
    }
    /////////////////////////////////////////////////////////////////////////

    /////////////////////////////////////////////////////////////////////////
    ////类别匹配相关操作
    /////////////////////////////////////////////////////////////////////////
    private void loadPositionLabelled() {
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
        System.out.println("labelled positions load finished, time elapsed " + (e - s) + " ms.");
    }

    /**
     * 类别匹配:如果是SEO标题返回1，否则返回0
     *
     * @param sen
     * @return
     */
    public int cat_match(String sen) {
        return cat_match(sen, 4);
    }

    private int cat_match(String sen, int threshold) {
        List<String> sen_words = seg_sentence(sen);
        int score = getScore(sen_words);
        return score >= threshold ? 1 : 0;
    }

    private int getScore(List<String> sen_words) {
        HashSet<Integer> tmp = new HashSet<>();
        for (String word : sen_words) {
            if (positions_labelled_dict.containsKey(word)) {
                tmp.add(positions_labelled_dict.get(word));
            }
        }
        return tmp.size();
    }
    /////////////////////////////////////////////////////////////////////////

    /**
     * 逻辑回归与类别匹配结合预测
     *
     * @param sen
     * @return
     */
    public int LR_CM_predict(String sen) {
        int res1 = this.predict(sen);
        int res2 = this.cat_match(sen);
        return (res1 == 1 && res2 == 1) ? 1 : 0;
    }
    //////////////////////////////////////////////////////////////////////////

    /**
     * LR 批量预测一个文件
     *
     * @param inputFile
     * @param outputFile1
     * @param outputFile0
     */
    public void solveSingleFile_LR(String inputFile, String outputFile1, String outputFile0) {
        solveSingleFile(inputFile, outputFile1, outputFile0, 0);
    }

    /**
     * 类别匹配 批量预测一个文件
     *
     * @param inputFile
     * @param outputFile1
     * @param outputFile0
     */
    public void solveSingleFile_CM(String inputFile, String outputFile1, String outputFile0) {
        solveSingleFile(inputFile, outputFile1, outputFile0, 1);
    }

    /**
     * LR与类别匹配结合批量预测一个文件
     *
     * @param inputFile
     * @param outputFile1
     * @param outputFile0
     */
    public void solveSingleFile_LR_CM(String inputFile, String outputFile1, String outputFile0) {
        solveSingleFile(inputFile, outputFile1, outputFile0, 2);
    }

    private void solveSingleFile(String inputFile, String outputFile1, String outputFile0, int SELECTED) {
        BufferedReader br = null;
        BufferedWriter out1 = null;
        BufferedWriter out0 = null;
        try {
            InputStreamReader reader = new InputStreamReader(new FileInputStream(inputFile));
            out1 = new BufferedWriter(new FileWriter(outputFile1));
            out0 = new BufferedWriter(new FileWriter(outputFile0));
            br = new BufferedReader(reader);
            String line;
            int predict = -1;
            while ((line = br.readLine()) != null) {
                line = line.trim().toLowerCase();
                if (SELECTED == 0) {
                    predict = this.predict(line);
                } else if (SELECTED == 1) {
                    predict = this.cat_match(line);
                } else if (SELECTED == 2) {
                    predict = this.LR_CM_predict(line);
                }
                if (predict == 1) {
                    out1.write(line);
                    out1.write("\n");
                } else if (predict == 0) {
                    out0.write(line);
                    out0.write("\n");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
                if (out1 != null) {
                    out1.close();
                }
                if (out0 != null) {
                    out0.close();
                }
            } catch (Exception e) {
            }
        }
    }
}