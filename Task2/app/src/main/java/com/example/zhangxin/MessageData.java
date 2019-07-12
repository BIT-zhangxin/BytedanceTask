package com.example.zhangxin;

import java.sql.Blob;
import java.sql.Timestamp;

public class MessageData {

    int id;//id
    String title;//标题
    String description;//描述
    Timestamp time;//时间戳
    Blob picture1;//图片1
    byte[] picture2;//图片2

}
