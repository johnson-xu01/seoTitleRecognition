# seoTitleRecognition
sentence classification: 识别出`一职多岗`的招聘标题，打上标签１．

将[word2vec_kmeans_LR](https://github.com/xuyiqiang-learn/word2vec_kmeans_LR)训练的部分结果移植为java版本．
```java
public class Example {
    public static void main(String[] args) {
        Anti_SEO_Solver anti_seo_solver = Anti_SEO_Solver.getInstance();
        anti_seo_solver.connect();
        int predict = anti_seo_solver.LR_CM_predict("直招：船员普工、跟单员、电焊工、厨师");
        System.out.println(predict);  // 1
        int predict1 = anti_seo_solver.LR_CM_predict("石油化工工艺工程师");
        System.out.println(predict1); // 0

    }
}
```
