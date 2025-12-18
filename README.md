# JFST
A Java implementation of Finite State Transducer

## Introduction to FST
FST(Finite state transducer) is a data structure that facilitate string match and producing an output. It functions as a Map and allows reverse and wild card search, and uses much less memory to store entries than a common Map because of the sharing of prefix and suffix.

For theoretical and implementation details, please refer to this blog https://burntsushi.net/transducers/.

## Usage example
```
public static String[] str2Array(String str){
        int len = str.length();
        String[] ret = new String[len];
        for (int i = 0; i < len; i++) {
            ret[i] =  String.valueOf(str.charAt(i));
        }
        return ret;
    }

public static void main(String[] args) {
    String[] examples = new String[]{"app", "apple", "applet", "aplet"};
    long[] outputs = new long[]{10, -1, 8, -6};
    // phrases must be sorted before adding into FST
    Arrays.sort(examples);
    ArrayList<Pair<Long, String[]>> inputs = new ArrayList<>();
    for (int i = 0; i < examples.length; i++){
        //the reason why an entry must be a string list is a node in FST does not have to be a character
        String[] phrase = str2Array(examples[i]);
        Pair<Long, String[]> entry = new Pair<>(outputs[i], phrase);
        inputs.add(entry);
    }

    FST fst = new FST();
    fst.build(inputs);
    String[] example1 = str2Array("app");
    String[] example2 = str2Array("et");
    System.out.println(fst.fuzzySearchPrefix(example1));
    System.out.println(fst.fuzzySearchSuffix(example2));
    System.out.println(fst.search(example1));
    System.out.println(fst.backSearch(example2));
    System.out.println(fst.backSearch(example1));
}

//print result 
[-1=[a, p, p], 8=[a, p, p, l, e], -6=[a, p, p, l, e, t]]
[10=[a, p, l, e, t], -6=[a, p, p, l, e, t]]
-1=[a, p, p]
0=[]
-1=[a, p, p]
```

## Environment configuration
* jdk 1.8+
* maven 3.8.1+

## License
Apache-2.0
