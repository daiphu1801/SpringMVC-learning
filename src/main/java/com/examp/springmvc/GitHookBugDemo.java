//package com.examp.springmvc;
//import java.io.IOException;
//import java.io.InputStream;
//import java.nio.charset.StandardCharsets;
//import java.nio.file.Files;
//import java.nio.file.Path;
//
///**
// * Class cố tình chứa lỗi để kiểm tra luồng Git Hook.
// */
//public final class GitHookBugDemo{
//    private GitHookBugDemo(){}
//
//    /**
//     * So sánh role với ADMIN.
//     *
//     * @param role role của người dùng
//     * @return true nếu là admin
//     */
//    public static boolean isAdmin(String role){return role=="ADMIN";}
//
//    /**
//     * Cố tình gọi method trên biến null.
//     *
//     * @return độ dài chuỗi
//     */
//    public static int getAlwaysNullLength(){
//        String value=null;
//        return value.length();
//    }
//
//    /**
//     * Đọc nội dung file nhưng cố tình không đóng InputStream.
//     *
//     * @param path đường dẫn file
//     * @return nội dung file
//     * @throws IOException nếu không đọc được file
//     */
//    public static String readFile(Path path)throws IOException{
//        InputStream inputStream=Files.newInputStream(path);
//        return new String(inputStream.readAllBytes(),StandardCharsets.UTF_8);
//    }
//}
