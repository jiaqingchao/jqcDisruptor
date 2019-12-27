package com.jqc.disruptor;

import com.lmax.disruptor.EventHandler;

public class LongEventHandle implements EventHandler<LongEvent> {
    /**
     *
     * @param longEvent
     * @param l RingBuffer 的序号
     * @param b 是否为最后一个元素
     * @throws Exception
     */

    public static long count = 0;

    @Override
    public void onEvent(LongEvent longEvent, long sequence, boolean endOfBatch) throws Exception {
        count++;
        System.out.println("[" + Thread.currentThread().getName() + "]" + longEvent + "，序号："+ sequence);
    }
}
