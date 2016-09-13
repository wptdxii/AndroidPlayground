package com.wptdxii.data.net.retrofit.converter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;

/**
 * Created by wptdxii on 2016/8/18 0018.
 */
public class FastJsonConfig {
    public static final Feature[] EMPTY_SERIALIZER_FEATURES = new Feature[0];


    private ParserConfig parserConfig;
    private int featureValues;
    private Feature[] features;

    private SerializeConfig serializeConfig;
    private SerializerFeature[] serializerFeatures;


    public FastJsonConfig() {
        this.parserConfig = ParserConfig.getGlobalInstance();
        this.featureValues = JSON.DEFAULT_PARSER_FEATURE;
        
//        this.serializeConfig = SerializeConfig.globalInstance;
//        this.serializerFeatures = SerializerFeature.values();
        
//        this.serializerFeatures = new SerializerFeature[] {
//                SerializerFeature.WriteNullBooleanAsFalse,//boolean为null时输出false
//                SerializerFeature.WriteMapNullValue, //输出空置的字段
//                SerializerFeature.WriteNonStringKeyAsString,//如果key不为String 则转换为String 比如Map的key为Integer
//                SerializerFeature.WriteNullListAsEmpty,//list为null时输出[]
//                SerializerFeature.WriteNullNumberAsZero,//number为null时输出0
//                SerializerFeature.WriteNullStringAsEmpty//String为null时输出""      
//        };
    }

    public ParserConfig getParserConfig() {
        return parserConfig;
    }
    
    public int getFeatureValues() {
        return featureValues;
    }
    
    public Feature[] getFeatures() {
        return features;
    }
    
    public SerializeConfig getSerializeConfig() {
        return serializeConfig;
    }

    public SerializerFeature[] getSerializerFeatures() {
        return serializerFeatures;
    }
}
