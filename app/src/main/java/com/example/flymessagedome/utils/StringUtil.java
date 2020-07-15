package com.example.flymessagedome.utils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class StringUtil {
    static final int GB_SP_DIFF = 160; // 存放国标一级汉字不同读音的起始区位码
    static final int[] secPosValueList = { 1601, 1637, 1833, 2078, 2274, 2302,
            2433, 2594, 2787, 3106, 3212, 3472, 3635, 3722, 3730, 3858, 4027,
            4086, 4390, 4558, 4684, 4925, 5249, 5600 }; // 存放国标一级汉字不同读音的起始区位码对应读音
    static final char[] firstLetter = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H',
            'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'W', 'X',
            'Y', 'Z' };
    static final char[] firstLetters = { 'a', 'b', 'b', 'b', 'e', 'f', 'g', 'h',
            'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'w', 'x',
            'y', 'z' };
 // 获取一个汉字的首字母

    public static Character getFirstLetter(char ch) {
        byte[] uniCode = null;
        try {
            uniCode = String.valueOf(ch).getBytes("GBK");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
        if (uniCode[0] < 128 && uniCode[0] > 0) { // 非汉字
            for (char c:
                 firstLetter) {
                if (c==ch){
                    return c;
                }
            }
            for (int i=0;i<firstLetter.length;i++){
                if (firstLetters[i]==ch){
                    return firstLetter[i];
                }
            }
            return null;
        } else {
            return convert(uniCode);
        }
    }

    static char convert(byte[] bytes) {
        char result = '-';
        int secPosValue = 0;
        int i;
        for (i = 0; i < bytes.length; i++) {
            bytes[i] -= GB_SP_DIFF;
        }
        secPosValue = bytes[0] * 100 + bytes[1];
        for (i = 0; i < 23; i++) {
            if (secPosValue >= secPosValueList[i]
                    && secPosValue < secPosValueList[i + 1]) {
                result = firstLetter[i];
                break;
            }
        }
        return result;
    }
    /**
     * 格式化带文件的消息内容，完成效果->[图片]
     * @param content
     * @return
     */
    public static String formatFileMessage(String content){
        try {
            if (content.contains("[")&&content.contains("]")){
                String fileContent=content.substring(content.indexOf("[")+1,content.indexOf("]"));
                if (fileContent.contains(",")) {
                    String[] params=fileContent.split(",");
                    if (params.length==2) {
                        if (params[0].contains(":")) {
                            String[] fileParams=params[0].split(":");
                            fileContent=fileParams[0];
                            if (fileContent.equals("picture")){
                                fileContent="[图片]";
                            }else if (fileContent.equals("video")){
                                fileContent="[视频]";
                            }else if (fileContent.equals("voice")){
                                fileContent="[语音]";
                            }else{
                                fileContent="[文件]";
                            }
                            String messageContent=content.substring(0,content.indexOf("["));
                            content=messageContent+fileContent;
                        }
                    }
                }
            }
        }catch (Exception e){
            return content;
        }
        return content;
    }
    public static MsgFile getMsgFile(String content){
        MsgFile msgFile=null;
        if (content.contains("[")&&content.contains("]")){
            String fileContent=content.substring(content.indexOf("[")+1,content.indexOf("]"));
            if (fileContent.contains(",")) {
                String[] params=fileContent.split(",",2);
                if (params.length==2) {
                    if (params[0].contains(":")) {
                        msgFile=new MsgFile();
                        String[] fileParams=params[0].split(":",2);
                        if (fileParams[1].length()>24) {
                            String[] fileNameParams=fileParams[1].split("\\.");
                            String fileType=fileNameParams[fileNameParams.length-1];
                            String fileName="";
                            for(int i=0;i<fileNameParams.length-1;i++){
                                fileName+=fileNameParams[i];
                            }
                            String tmp1,tmp2;
                            if (fileName.length()>20){
                                tmp1=fileName.substring(0,10);
                                tmp2=fileName.substring(fileName.length()-10,fileName.length());
                                fileName=tmp1+"..."+tmp2;
                            }
                            fileParams[1]=fileName+"."+fileType;
                        }
                        msgFile.setFilename(fileParams[1]);
                        msgFile.setFileType(fileParams[0]);
                        String messageContent=content.substring(0,content.indexOf("["));
                        msgFile.setContent(messageContent);
                        if (params[1].contains(":")){
                            String[] linkParams=params[1].split(":",2);
                            if (linkParams[0].equals("link")){
                                msgFile.setLink(linkParams[1]);
                                return msgFile;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    public static class MsgFile{
        String content;
        String fileType;
        String filename;
        String link;

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getFileType() {
            return fileType;
        }

        public void setFileType(String fileType) {
            this.fileType = fileType;
        }

        public String getFilename() {
            return filename;
        }

        public void setFilename(String filename) {
            this.filename = filename;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }

        @Override
        public String toString() {
            return "["+fileType+":"+filename+",link:"+link+"]";
        }
    }
}
