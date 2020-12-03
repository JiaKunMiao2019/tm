/**
 * Copyright (C), 2015-2020, XXX有限公司
 * FileName: QRCommonBO
 * Author:   莉莉
 * Date:     2020/11/30 17:23
 * Description: 二维码使用的参数BO
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.szdq.tm.serverprint.common;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 〈二维码使用的参数BO〉
 *
 * @author 莉莉
 * @create 2020/11/30
 * @since 1.0.0
 */
@Setter
@Getter
@ToString
public class QRCommonBO {
     //图片的宽度和高度
    int width;
    int height;
    // 图片的格式
    String format;
    //内容
    String content;
    //保存地址
    String path;
    //二维码名称
    String qrCodeName;
}