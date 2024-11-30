package com.connectus.utility;
import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Collectors;


public class CodeGenerator {

    public static String generateActivationCode() {
        String string = UUID.randomUUID().toString();

        String[] split =string.split("-");
        StringBuilder stringBuilder = new StringBuilder();

        for (String s : split) {
            stringBuilder.append(s.charAt(0));
        }

        return stringBuilder.toString();
    }
    public static String generateResetPasswordCode() {
        String string = UUID.randomUUID().toString();

        String[] split =string.split("-");
        StringBuilder stringBuilder = new StringBuilder();

        for (String s : split) {
            stringBuilder.append(s.charAt(0));
        }

        return stringBuilder.toString();
    }
    public static String getActivationCode(){
        String uuid = UUID.randomUUID().toString();
        return Arrays.stream(uuid.split("-")).map(segment -> String.valueOf(segment.charAt(0)))
                .collect(Collectors.joining());
    }
}