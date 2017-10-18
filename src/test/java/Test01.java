import com.huaban.analysis.jieba.JiebaSegmenter;

import java.util.List;

public class Test01 {
        public static void main(String[] args) {
        JiebaSegmenter segmenter = new JiebaSegmenter();//加载字典
        String sen = "直通车推广的朋友欢迎加入".toLowerCase();
        List<String> words = segmenter.sentenceProcess(sen);
        System.out.println(words);
    }
}
