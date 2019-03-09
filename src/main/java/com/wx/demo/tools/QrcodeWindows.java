package com.wx.demo.tools;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;

public class QrcodeWindows {
    public static JFrame jf = new JFrame();



    public static void getQrcode(String name,String ImgBuf) {
        if(name == null || name.equals("")){
            name = "请打开手机摄像头扫描二维码!";
        }
        jf = new JFrame(name);
        getQrcode(Base64.getDecoder().decode(ImgBuf));
    }

    public static void overQrcode() {
        jf.dispose();
    }

    public static void getQrcode(byte[] ImgBuf) {
        String os = System.getProperty("os.name");
        if (os.toLowerCase().startsWith("win")) {
            byte[] bs =ImgBuf;
            ByteArrayInputStream bis = new ByteArrayInputStream(bs);
            BufferedImage img = null;
            try {
                img = ImageIO.read(bis);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            jf.setSize(200, 220);
            //jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            jf.setLocationRelativeTo(null);
            ImageIcon icon = new ImageIcon(img.getScaledInstance(200, 200, BufferedImage.SCALE_DEFAULT));
            JLabel label = new JLabel(icon);
            jf.add(label);
            jf.setVisible(true);
        }
    }





}
