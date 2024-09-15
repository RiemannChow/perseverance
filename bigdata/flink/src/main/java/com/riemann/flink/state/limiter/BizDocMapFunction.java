package com.riemann.flink.state.limiter;

public class BizDocMapFunction extends AbstractRichMapFunction<String, BizDoc> {


    @Override
    public BizDoc map(String value) {

        BizDoc bizDoc = new BizDoc();
        //把value转换成bizDoc

        return bizDoc;
    }
}
