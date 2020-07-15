package com.example.flymessagedome.utils;

import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;

import java.util.ArrayList;

public class TextSpanUtil{
    /**
     *
     * @param text  文字的总内容
     * @param indexStr 关键字的内容
     * @param color  关键字的颜色
     * @return
     */
    public SpannableStringBuilder setColor(String text, String indexStr, int color) {

        //  记录关键字的次数 与他在整个字符中所占的索引位置
        String[] deStr = text.split(indexStr);
        ArrayList<Entity> objects = new ArrayList<>(deStr.length);
        int cycleSize = text.endsWith(indexStr) ? deStr.length : deStr.length - 1;
        for (int i = 0; i < cycleSize; i++) {
            Entity entity = new Entity();
            int index;
            if (i == 0) {
                index = text.indexOf(indexStr);
            } else {
                Entity entity1 = objects.get(i - 1);
                index = text.indexOf(indexStr, entity1.end);
            }
            entity.start = index;
            entity.end = index + indexStr.length();
            objects.add(entity);
        }


        //根据索引集合 来设置关键字 字体颜色
        SpannableStringBuilder builder = new SpannableStringBuilder(text);

        for (int i = 0; i < objects.size(); i++) {
            //这个实例设置一次关键字的时候就要重新实例一次
            ForegroundColorSpan redSpan = new ForegroundColorSpan(color);
            builder.setSpan(redSpan, objects.get(i).start, objects.get(i).end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        return builder;
    }
    private static volatile TextSpanUtil mTextSpanUtil;

    public static TextSpanUtil getInstant() {
        if (null == mTextSpanUtil) {
            synchronized (TextSpanUtil.class) {
                if (null == mTextSpanUtil) {
                    mTextSpanUtil = new TextSpanUtil();
                }
            }
        }
        return mTextSpanUtil;
    }

    public class Entity {
        public int start;
        public int end;
        @Override
        public String toString() {
            return "Entity{" +
                    "start=" + start +
                    ", end=" + end +
                    '}';
        }
    }
}


