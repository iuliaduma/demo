package com.example.demo.data.user;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum Title {

    MS,
    MR;

    private static Map<String, Title> codes;

    public static Title enumValue(String title) {
        if (codes == null) {
            codes = createKeyMap();
        }

        return codes.get(title);
    }

    private static Map<String, Title> createKeyMap() {
        return Arrays.stream(Title.values())
                .collect(Collectors.toMap(Title::name, value -> value));
    }
}