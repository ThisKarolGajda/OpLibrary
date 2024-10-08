package me.opkarol.oplibrary.misc;

import me.opkarol.oplibrary.inventories.ItemBuilder;
import me.opkarol.oplibrary.tools.Heads;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class Books {
    private static final Set<String> BOOKS = new HashSet<>();

    public static ItemBuilder getRandomHead(String key) {
        if (key == null) {
            throw new IllegalArgumentException("Key is null");
        }

        int hash = customHash(key);
        String[] values = BOOKS.toArray(new String[0]);
        return Heads.get(values[Math.abs(hash) % values.length]);
    }

    private static int customHash(@NotNull String key) {
        int hash = 0;
        for (int i = 0; i < key.length(); i++) {
            hash += key.charAt(i);
        }
        return hash;
    }

    static {
        BOOKS.add("1d111a029754d5d2681b65ade843b721d0a814a80dc16a38ea04cae61913ae20");
        BOOKS.add("c2ebbdb18d747281b5462f857ee984675a39d5a0274446a22f66264a53d2b034");
        BOOKS.add("d79f64258b47d34a44f74f7e75b9858cdd52fd90af8109036a8077fef3c70885");
        BOOKS.add("3c1903400d856d0570a06d24f9aade218ba7ae898221295c0b36de655c2dc765");
        BOOKS.add("fd723853b0f19e4bf55d8ab9a953b4477328d3b20f77f304e37297fed752f942");
        BOOKS.add("fd99e09b0d89143eb2bb4796183a62dca22c7faae470258a480833169b02f96c");
        BOOKS.add("d0bbc97f7183cde2d99e9abeb2b241d56eeba180c43e235383ea4eadf0835623");
        BOOKS.add("b1a3393cc1226493dd6e780d71091b0b49eae19f861cb69651021a7cf7da9e3a");
        BOOKS.add("a260184a428c166fa6a986f33c61819ef6fa868d3f9bcc93620c4a293850b4e4");
        BOOKS.add("62e5ef8ff0517a82b5e965d549ecad0b07b7c474ad905f8879f15dbfc23454aa");
        BOOKS.add("b23e20e48ffce94f5de555370801c3045d3bd41063fce99b29695a523355aa3");
        BOOKS.add("27da07fc0e4ae101cd3d6b6080b04aa75381ef8074e02ed5397594f49b71cb0c");
        BOOKS.add("77d4dc645683f61765eddc100882c2a6562e0aa5333c907375d933b3aeece61b");
        BOOKS.add("c2df944b47420a37dfa8232f74a6e57bc35beb27edb608d60f04710e4f2378dd");
        BOOKS.add("9cba81da6c3ab4e0b3ac771efc52c23a5e01f260e49feaf4b4779c264bac91a4");
        BOOKS.add("84ffa0fde2ef3b7cdff6421dbbddbe32965cbbe1f7b8da192182d40b86d2d95c");
        BOOKS.add("9ca9cfeec0c6f2d0c1b295baf2dffe000d4b9f3725827f1dd67eb46ac2aad656");
        BOOKS.add("a260bc5bfc115cccc1323c74736c794de884ad011c91322edbe5c01f6a94305d");
        BOOKS.add("71da1a7af8f287aa5d09fa8a477039b469b2fd2b363f77293260e7a7e3582088");
    }
}
