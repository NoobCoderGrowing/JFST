[中文](#中文) | [English](#english)

# JFST

## 中文

Finite State Transducer 的 Java 实现

### FST 简介

FST（Finite state transducer）是一种用于字符串匹配的数据结构，并且在字符串匹配的同时产生一个输出。它的作用类似于一个 Map，并且支持倒序搜索和通配符搜索。FST 相较于 Map 而言使用的内存大大减少，因为这种数据结构在存储字符串的过程中支持前缀和后缀的共享。

如果想要了解更多的理论和实现细节，请参考这篇博客 https://burntsushi.net/transducers/

### 快速开始

引入maven依赖
```
<dependency>
    <groupId>io.github.noobcodergrowing</groupId>
    <artifactId>JFST</artifactId>
    <version>1.0.1</version>
</dependency>

```

### 使用案例

```
import io.github.noobcodergrowing.JFST.FST;
import io.github.noobcodergrowing.JFST.fstPair;

import java.util.ArrayList;
import java.util.Arrays;

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
    // 短语在加入 FST 前必须排序
    Arrays.sort(examples);
    ArrayList<fstPair<String[], Long>> inputs = new ArrayList<>();
    for (int i = 0; i < examples.length; i++){
        // 在 FST 中一个节点不一定非要是一个字符
        String[] phrase = str2Array(examples[i]);
        fstPair<String[], Long> entry = new fstPair<>(phrase, outputs[i]);
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

// 打印结果
[[a, p, p]=-1, [a, p, p, l, e]=8, [a, p, p, l, e, t]=-6, [a, p, p, l, e, t, l, e, t]=-3]
[[a, p, p, l, e, t]=-6, [a, p, l, e, t]=10, [a, p, p, l, e, t, l, e, t]=-3]
[a, p, p]=-1
[]=0
[a, p, p]=-1
```

#### 案例内存结构

```mermaid
flowchart LR
    ROOT(["root"])
    A["a"]
    P1["p"]
    L1["l"]
    E1["e"]
    T1["t"]
    P2["p"]
    L2["l"]
    E2["e"]
    T2["t"]
    END(["end"])

    ROOT -->|"10"| A
    A -->|"0"| P1
    P1 -->|"0"| L1
    L1 -->|"0"| E1
    E1 -->|"0"| T1
    T1 -->|"0"| END
    P1 -->|"-11"| P2
    P2 -->|"0"| END
    P2 -->|"9"| L2
    L2 -->|"0"| E2
    E2 -->|"0"| END
    E2 -->|"-14"| T2
    T2 -->|"0"| END
    T2 -->|"3"| L1

    style ROOT fill:#e8f4fd
    style END fill:#fde8e8
```

### 环境配置

* jdk 1.8+
* maven 3.8.1+

### 许可证协议

Apache-2.0

---

## English

A Java implementation of Finite State Transducer

### Introduction to FST

FST (Finite state transducer) is a data structure that facilitates string match and produces an output. It functions as a Map and allows reverse and wild card search, and uses much less memory to store entries than a common Map because of the sharing of prefix and suffix.

For theoretical and implementation details, please refer to this blog https://burntsushi.net/transducers/


### Quick start

import maven dependency
```
<dependency>
    <groupId>io.github.noobcodergrowing</groupId>
    <artifactId>JFST</artifactId>
    <version>1.0.0</version>
</dependency>

```

### Usage example

```
import io.github.noobcodergrowing.JFST.FST;
import io.github.noobcodergrowing.JFST.fstPair;

import java.util.ArrayList;
import java.util.Arrays;

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
        // the reason why an entry must be a string list is a node in FST does not have to be a character
        String[] phrase = str2Array(examples[i]);
        fstPair<String[], Long> entry = new fstPair<>(phrase, outputs[i]);
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

// print result
[[a, p, p]=-1, [a, p, p, l, e]=8, [a, p, p, l, e, t]=-6, [a, p, p, l, e, t, l, e, t]=-3]
[[a, p, p, l, e, t]=-6, [a, p, l, e, t]=10, [a, p, p, l, e, t, l, e, t]=-3]
[a, p, p]=-1
[]=0
[a, p, p]=-1
```

#### Example memory layout

```mermaid
flowchart LR
    ROOT(["root"])
    A["a"]
    P1["p"]
    L1["l"]
    E1["e"]
    T1["t"]
    P2["p"]
    L2["l"]
    E2["e"]
    T2["t"]
    END(["end"])

    ROOT -->|"10"| A
    A -->|"0"| P1
    P1 -->|"0"| L1
    L1 -->|"0"| E1
    E1 -->|"0"| T1
    T1 -->|"0"| END
    P1 -->|"-11"| P2
    P2 -->|"0"| END
    P2 -->|"9"| L2
    L2 -->|"0"| E2
    E2 -->|"0"| END
    E2 -->|"-14"| T2
    T2 -->|"0"| END
    T2 -->|"3"| L1

    style ROOT fill:#e8f4fd
    style END fill:#fde8e8
```

### Environment configuration

* jdk 1.8+
* maven 3.8.1+

### License

Apache-2.0
