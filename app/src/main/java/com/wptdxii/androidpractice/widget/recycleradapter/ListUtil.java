package com.wptdxii.androidpractice.widget.recycleradapter;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by wptdxii on 16/7/13.
 */
public class ListUtil {

    /**
     *
     * @param sourceList 源对象列表
     * @param converter 转换器
     * @param <S> 源对象
     * @param <T> 目标对象
     * @return
     */
    public static <S,T> List<T> transform(List<S> sourceList , Converter<S,T> converter) {
        List<T> targetList = new ArrayList<>();
        if (sourceList == null || converter == null) {
            return targetList;
        }
        for (S source : sourceList) {
            targetList.add(converter.transform(source));
        }
        return targetList;
    }

    public static <S> void operate(List<S> sourceList, Operation<S> operation) {
        if (sourceList == null || operation == null) {
            return;
        }
        for (S source : sourceList) {
            operation.operate(source);
        }
    }

    /**
     * 转换器
     * @param <S> 源对象
     * @param <T> 转换后对象
     */
    public interface Converter<S,T>  {
        /**
         *
         * @param source 待转换的对象
         * @return 转换后的对象
         */
        T transform(S source);
    }

    /**
     * 操作执行器
     * @param <S> 待执行操作的类
     */
    public interface Operation<S> {
        /**
         * 待操作的对象
         * @param source
         */
        void operate(S source);
    }
}
