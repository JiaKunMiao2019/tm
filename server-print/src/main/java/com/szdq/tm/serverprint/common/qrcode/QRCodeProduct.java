/**
 * Copyright (C), 2015-2020, XXX有限公司
 * FileName: QRCodeProduct
 * Author:   莉莉
 * Date:     2020/11/30 11:13
 * Description: 二维码生成
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.szdq.tm.serverprint.common.qrcode;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.*;
import com.szdq.tm.serverprint.common.QRCommonBO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

/**
 * 〈二维码生成〉
 *
 * @author 莉莉
 * @create 2020/11/30
 * @since 1.0.0
 */
@Slf4j
public class QRCodeProduct {
    /**
     * 生成二维码保存到本地
     */
    public Boolean getQRCodeAndSave(QRCommonBO qrCommonBO) {
        //获得二维码的宽度和高度
        int width = qrCommonBO.getWidth();
        int height = qrCommonBO.getHeight();

        // 图片的格式
        String format = qrCommonBO.getFormat();
        //获得内容参数
        String content = qrCommonBO.getContent();
        //获得存储路径
        String path = qrCommonBO.getPath();
        //二维码名称
        String qrCodeName = qrCommonBO.getQrCodeName();
        // 定义二维码的参数
        HashMap<EncodeHintType, Object> hints = new HashMap<>();
        // 定义字符集编码格式
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        // 纠错的等级 L > M > Q > H 纠错的能力越高可存储的越少，一般使用M
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
        // 设置图片边距
        hints.put(EncodeHintType.MARGIN, 2);

        // 最终生成 参数列表 （1.内容 2.格式 3.宽度 4.高度 5.二维码参数）
        ByteArrayOutputStream bos = null;
        BufferedOutputStream out = null;
        try {
            BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hints);
            //删除空白边框
            bitMatrix = deleteWhite(bitMatrix);
            //给二维码下面增加文字
            BufferedImage image = null;
            image = insertText(bitMatrix, qrCodeName);
            bos = new ByteArrayOutputStream();
            if (image != null) {
                ImageIO.write(image,format,bos);
            }

            File file = new File(path+qrCodeName+"."+format);
            // 写入到本地
            //TODO
            out = new BufferedOutputStream(
                    new FileOutputStream(file));  //你要保存在哪个目录下面
            int i;
            bos = new ByteArrayOutputStream();
            ImageIO.write(image,format,bos);
            byte[] bytes= bos.toByteArray();

            out.write(bytes);

            out.flush();
            out.close();
            //MatrixToImageWriter.writeToPath(bitMatrix, format, file);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**把文字添加到二维码*/
    private BufferedImage insertText(BitMatrix bitMatrix, String qrCodeName) {
        if (bitMatrix != null && !StringUtils.isEmpty(qrCodeName) && !StringUtils.isEmpty(qrCodeName.trim())) {
            BufferedImage image = MatrixToImageWriter.toBufferedImage(bitMatrix);
            int width = image.getWidth();
            int height = image.getHeight();
            int textHeight = 16;
            BufferedImage outImage = new BufferedImage(width, height + textHeight, BufferedImage.TYPE_4BYTE_ABGR);
            Graphics2D outg = outImage.createGraphics();
            //画二维码到新的面板
            outg.drawImage(image, 0, 0, width, height, null);
            outg.setColor(Color.BLACK);
            outg.setFont(new Font("宋体", Font.BOLD, 12));
            int strWidth = outg.getFontMetrics().stringWidth(qrCodeName);
            if (strWidth < width) {
                outg.drawString(qrCodeName, (width - strWidth) / 2, width + (outImage.getWidth() - height) / 2 + 4);
                outg.dispose();
                outImage.flush();
                return outImage;
            } else {
                return null;
            }
        }
        return null;
    }

    /**
     * 返回字节二维码
     */
    public byte[] getQRCode(QRCommonBO qrCommonBO) {
        //获得二维码的宽度和高度
        int width = qrCommonBO.getWidth();
        int height = qrCommonBO.getHeight();
        //获得内容参数
        String content = qrCommonBO.getContent();

        Hashtable hints = new Hashtable();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        hints.put(EncodeHintType.MARGIN, 0);//空白边框
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, width, height, hints);
            ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
            byte[] pngData = pngOutputStream.toByteArray();
            log.info("返回数据流长度：{}", pngData.length);
            return pngData;
        } catch (WriterException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 返回二维码图片
     */
    public ResponseEntity<byte[]> returnQRImage(byte[] qrcode) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);
        return new ResponseEntity<byte[]>(qrcode, headers, HttpStatus.CREATED);
    }

    /**
     * 向PDF文件添加二维码
     */
    public byte[] getQrCodeImage(byte[] qrBytes, byte[] pdfValue) {
        int qrImageX = 500;
        int qrImageY = 700;
        PdfStamper stamper = null;
        PdfReader pdfReader = null;
        ByteArrayOutputStream bos = null;
        try {
            //将二维码的字节流转换为图片
            Image image = Image.getInstance(qrBytes);

            //新建PDF的阅读器
            bos = new ByteArrayOutputStream();
            pdfReader = new PdfReader(pdfValue);
            stamper = new PdfStamper(pdfReader, bos);

            //确认二维码的添加在哪也
            PdfContentByte overContent = stamper.getOverContent(1);

            //设置二维码图片坐标
            image.setAbsolutePosition(qrImageX, qrImageY);

            //添加二维码水印到pdf
            overContent.addImage(image);

            stamper.close();
            pdfReader.close();
        } catch (BadElementException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(bos);
        }
        return bos.toByteArray();
    }

    /**
     * 多个个PDF文件流进行合并
     */
    public byte[] mergePDFs(List<byte[]> pdfStreams) {
        try {
            //首先判断文件流是否为空
            if (CollectionUtils.isEmpty(pdfStreams)) {
                return null;
            }

            ByteArrayOutputStream bos = new ByteArrayOutputStream();

            //创建write
            Document document = new Document();
            PdfWriter writer = PdfWriter.getInstance(document, bos);
            //设置图片是否精确位置
            writer.setStrictImageSequence(true);
            document.open();
            PdfContentByte cb = writer.getDirectContent();

            for (byte[] pdf : pdfStreams) {
                //创建一个reader
                PdfReader reader = new PdfReader(pdf);
                int pageCurrentReaderPDF = 0;
                //每个PDF，新的一页开始
                while (pageCurrentReaderPDF < reader.getNumberOfPages()) {//这一个pdfReader一共需要些多少页
                    document.setPageSize(reader.getPageSize(++pageCurrentReaderPDF));//设置拼接后新一页的大小与原来一样
                    document.newPage();//每个byte[]从新的一页开始，同时写完一页就新建一页
                    PdfImportedPage page = writer.getImportedPage(reader, pageCurrentReaderPDF);//写pdfReader的哪一页
                    cb.addTemplate(page, 0, 0);//开始写PDFReader指定的页的数据到新的一页
                }
            }
            bos.flush();
            bos.close();
            document.close();
            return bos.toByteArray();

        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new byte[0];
    }

    /**
     * 删除二维码空白边框
     */
    private BitMatrix deleteWhite(BitMatrix bitMatrix) {
        int[] rec = bitMatrix.getEnclosingRectangle();
        int restWidth = rec[2] + 1;
        int restHeigth = rec[3] + 1;

        BitMatrix matrix = new BitMatrix(restWidth, restHeigth);
        matrix.clear();
        for (int i = 0; i < restWidth; i++) {
            for (int j = 0; j < restHeigth; j++) {
                if (bitMatrix.get(i + rec[0], j + rec[1])) {
                    matrix.set(i, j);
                }
            }
        }
        return matrix;
    }

}