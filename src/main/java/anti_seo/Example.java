package anti_seo;

/**
 * API使用示例
 */
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
