package com.richeninfo.util;

import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;

/**
 * @Author : zhouxiaohu
 * @create 2022/12/12 15:20
 */
@Component
public class ImageUtil {
    /**
     * 验证码图片的宽度。
     */
    private int width = 200;

    /**
     * 验证码图片的高度。
     */
    private int height = 100;

    /**
     * 验证码字符个数
     */
    private int codeCount = 4;

    /**
     * xx
     */
    private int xx = 0;

    /**
     * 字体高度
     */
    private int fontHeight;

    /**
     * codeY
     */
    private int codeY;

    /**
     * codeSequence
     */
    String[] codeSequence = { "1", "2", "3", "4", "5", "6", "7", "8", "9" };

    /**
     * 初始化验证图片属性
     */
    public void init() throws ServletException {
        // 从web.xml中获取初始信息
        // 宽度
        String strWidth = width + "";
        // 高度
        String strHeight = height + "";
        // 字符个数
        String strCodeCount = codeCount + "";
        // 将配置的信息转换成数值
        try {
            if (strWidth != null && strWidth.length() != 0) {
                width = Integer.parseInt(strWidth);
            }
            if (strHeight != null && strHeight.length() != 0) {
                height = Integer.parseInt(strHeight);
            }
            if (strCodeCount != null && strCodeCount.length() != 0) {
                codeCount = Integer.parseInt(strCodeCount);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        xx = width / (codeCount + 2); // 生成随机数的水平距离
        fontHeight = height - 12; // 生成随机数的数字高度
        codeY = height - 8; // 生成随机数的垂直距离
    }


    /**
     * 生成随机验证码及图片
     * Object[0]：验证码字符串；
     * Object[1]：验证码图片。
     */
    public Object[] createImage() throws ServletException {
        init();
        // 定义图像buffer
        BufferedImage buffImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D gd = buffImg.createGraphics();
        // 创建一个随机数生成器类
        Random random = new Random();
        // 将图像填充为白色
        gd.setColor(Color.white);
        gd.fillRect(0, 0, width, height);
        // 创建字体，字体的大小应该根据图片的高度来定。
        Font font = new Font("Microsoft YaHei", Font.PLAIN, fontHeight);
        // 设置字体。
        gd.setFont(font);
        // 画边框。
        gd.setColor(Color.white);
        gd.drawRect(0, 0, width - 1, height - 1);
        // randomCode用于保存随机产生的验证码，以便用户登录后进行验证。
        StringBuffer randomCode = new StringBuffer();
        // 随机产生codeCount数字的验证码。
        for (int i = 0; i < codeCount; i++) {
            // 得到随机产生的验证码数字。
            String strRand = String.valueOf(codeSequence[random.nextInt(9)]);
            // 用随机产生的颜色将验证码绘制到图像中。
            gd.setColor(Color.pink);
            gd.drawString(strRand, (i + 1) * xx, codeY);
            // 将产生的四个随机数组合在一起。
            randomCode.append(strRand);
        }
        // 7.返回验证码和图片
        return new Object[]{randomCode.toString(), buffImg};
    }

    /**
     * 随机取色
     */
    public static Color getRandomColor() {
        Random ran = new Random();
        Color color = new Color(ran.nextInt(256),
                ran.nextInt(256), ran.nextInt(256));
        return color;
    }
}