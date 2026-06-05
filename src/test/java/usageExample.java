import java.util.ArrayList;
import java.util.Arrays;

public class usageExample {

    public static String[] str2Array(String str){
        int len = str.length();
        String[] ret = new String[len];
        for (int i = 0; i < len; i++) {
            ret[i] =  String.valueOf(str.charAt(i));
        }
        return ret;
    }

    public static void main(String[] args) {
        String[] examples = new String[]{"aplet","app","apple", "applet","appletlet"};
        long[] outputs = new long[]{10, -1, 8, -6, -3};
        // phrases must be sorted before adding into FST
        Arrays.sort(examples);
        ArrayList<fstPair<String[], Long>> inputs = new ArrayList<>();
        for (int i = 0; i < examples.length; i++){
            //the reason why an entry must be a string list is a node in FST does not have to be a character
            String[] phrase = str2Array(examples[i]);
            fstPair<String[], Long> entry = new fstPair<>(phrase, outputs[i]);
            inputs.add(entry);
        }

        FST fst = new FST();
        fst.build(inputs);
        String[] app = str2Array("app");
        String[] et = str2Array("et");
        System.out.println(fst.fuzzySearchPrefix(app));
        System.out.println(fst.fuzzySearchSuffix(et));
        System.out.println(fst.search(app));
        System.out.println(fst.backSearch(et));
        System.out.println(fst.backSearch(app));

    }


}
