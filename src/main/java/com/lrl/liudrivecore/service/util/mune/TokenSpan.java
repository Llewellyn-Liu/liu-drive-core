package com.lrl.liudrivecore.service.util.mune;

public enum TokenSpan {

    INITIAL_SPAN(15 * 60 * 1000), SHORT_SPAN(5 * 60 * 1000), LONG_SPAN(10 * 60 * 1000), TEST_SPAN(60 * 1000);
    long span;

    TokenSpan(long spanInput){
        span = spanInput;
    }

    public long showSpan(){
        return span;
    }
}