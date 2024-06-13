/*
 * Copyright (c) RICHENINFO [2024]
 * Unauthorized use, copying, modification, or distribution of this software
 * is strictly prohibited without the prior written consent of Richeninfo.
 * https://www.richeninfo.com/
 *
 */

package com.richeninfo.util;

import com.univocity.parsers.common.processor.BeanListProcessor;
import com.univocity.parsers.csv.CsvFormat;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.mozilla.universalchardet.UniversalDetector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.List;

/**
 * @Author : zhouxiaohu
 * @create 2024/5/23 10:16
 */
@Component
public class FileUtil {

    private static String filePath = "/Users/zhouxiaohu/Desktop";
    // CSV上传路径
    private static String FILE_UPLOAD_SUB_CSV_DIR = "/csv";
    /**
     * 获取文件名称
     * @param str
     * @return
     */
    private String getFileName(String str) {
        int index = str.lastIndexOf("//");
        if (-1 != index) {
            return str.substring(index);
        } else {
            return str;
        }
    }
    /**
     * 创建目录
     * @param request
     * @return
     * @throws Exception
     */
    private File buildCsvFolder(HttpServletRequest request) throws Exception {
        // 这里照顾一下CKEDITOR，由于ftl放置位置的原因，这里必须要在freemarker目录下才能被加载到图片，否则虽然可以正常上传和使用，但是
        // 在控件中无法正常操作
        String realPath = filePath;
        File file = new File(realPath);
        if (!file.exists()) {
            if (!file.mkdir()) {
                return null;
            }
        }
        // 一级目录，如果不存在，创建
        File floder = new File(realPath + FILE_UPLOAD_SUB_CSV_DIR);
        if (!floder.exists()) {
            if (!floder.mkdir()) {
                return null;
            }
        }
        return floder;
    }
    /**
     * 文件上传
     * @param request
     * @param response
     * @param file
     */
    public void attachmentUpload(HttpServletRequest request, HttpServletResponse response, MultipartFile file){
        try {
            if (file != null) {
                response.setContentType("text/html; charset=UTF-8");
                response.setHeader("Cache-Control", "no-cache");
                PrintWriter out = response.getWriter();
                System.out.println("上传文件名:===========" + file.getOriginalFilename());
                String suffix = StringUtils.substring(file.getOriginalFilename(), file.getOriginalFilename().lastIndexOf(".") + 1);
                if (!StringUtils.equalsIgnoreCase(suffix, "csv")) {
                    System.out.println("上传文件的格式错误=" + suffix);
                    out.print("<script type=\"text/javascript\">alert('格式错误，仅支持CSV格式');</script>");
                    out.flush();
                    out.close();
                    return ;
                }
                if("csv".equals(suffix)){
                    String fileUrl = csvUpload(request, file, suffix);
                    if(!StringUtils.isBlank(fileUrl)){
                        out.print(fileUrl);
                        out.flush();
                        out.close();
                        return;
                    }
                }
                out.print("<script type=\"text/javascript\">alert('文件失败上传,请稍后重试～');</script>");
                out.flush();
                out.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("文件上传失败：===" + e.getMessage());
            try {
                PrintWriter out = response.getWriter();
                out.print("<script type=\"text/javascript\">alert('上传文件失败');</script>");
                out.flush();
                out.close();
            } catch (IOException ex) {
            }
        }
        return ;
    }
    private String csvUpload(HttpServletRequest request, MultipartFile file, String suffix) throws Exception{
        // 获取目录
        File folder = buildCsvFolder(request);
        if(folder == null){
            throw new RuntimeException("创建文件夹失败");
        }
        // 上传文件的返回地址
        String fileClientName = getFileName(file.getOriginalFilename());
        // 为了客户端已经设置好了图片名称在服务器继续能够明确识别，这里不改名称
        File newFile = new File(folder, fileClientName);
        System.out.println("新地址"+newFile.getPath());
        file.transferTo(newFile);
        return fileClientName;
    }
    /**
     * 读取crv文件并转换成List
     *
     * @param clazz  bean对象
     * @param file   待读取文件
     * @return crv对象list
     */
    public static <T> List<T> read(MultipartFile file, Class<T> clazz, String charset) {

        List<T> result = Collections.emptyList();
        try {
            BeanListProcessor<T> rowProcessor = new BeanListProcessor<>(clazz);
            InputStream in = file.getInputStream();
//          InputStreamReader reader = new InputStreamReader(in, "GBK");
            InputStreamReader reader = new InputStreamReader(in, charset);

            CsvParserSettings settings = new CsvParserSettings();
            settings.getFormat().setLineSeparator("\n");
            settings.setProcessor(rowProcessor);
            settings.setFormat(new CsvFormat());
            CsvParser parser = new CsvParser(settings);
            parser.parse(reader);
            //逐行读取
            result = rowProcessor.getBeans();
        } catch (Exception e) {
            System.out.println(e);
        }
        return result;
    }


    /**
     * 获取CSV文件编码格式
     * @param inputStream
     * @return
     * @throws IOException
     */
    public static String getCsvCharset(InputStream inputStream) throws IOException {
        byte[] buf = new byte[4096];
        UniversalDetector detector = new UniversalDetector(null);
        int nread;
        while ((nread = inputStream.read(buf)) > 0 && !detector.isDone()) {
            detector.handleData(buf, 0, nread);
        }
        detector.dataEnd();

        String encoding = detector.getDetectedCharset();
        detector.reset();
        return encoding;
    }
}
