/**
 * Copyright (C), 2015-2020, XXX有限公司
 * FileName: PdfPrintGetStream
 * Author:   莉莉
 * Date:     2020/11/20 16:49
 * Description: 获取PDF打印返回数据
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.szdq.tm.serverprint.common;

import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;

/**
 * 〈获取PDF打印返回数据〉
 *
 * @author 莉莉
 * @create 2020/11/20
 * @since 1.0.0
 */
public class PdfPrintGetStream {
  public void getDocument(){
      try {
          // 1.新建document对象
          Document document = new Document(PageSize.A4);// 建立一个Document对象

          // 2.建立一个书写器(Writer)与document对象关联
          File file = new File("D:\\PDFDemo1.pdf");
          file.createNewFile();
          PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(file));
          writer.setPageEvent(new Watermark("HELLO ITEXTPDF"));// 水印
          writer.setPageEvent(new MyHeaderFooter());// 页眉/页脚

          // 3.打开文档
          document.open();
          document.addTitle("Title@PDF-Java");// 标题
          document.addAuthor("Author@umiz");// 作者
          document.addSubject("Subject@iText pdf sample");// 主题
          document.addKeywords("Keywords@iTextpdf");// 关键字
          document.addCreator("Creator@umiz`s");// 创建者

          // 4.向文档中添加内容
          new PdfPrintUtils().generatePDF(document);

          // 5.关闭文档
          document.close();
      } catch (Exception e) {
          e.printStackTrace();
      }
  }

}