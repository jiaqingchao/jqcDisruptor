package com.jqc.disruptor;

import com.lmax.disruptor.*;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.util.DaemonThreadFactory;

public class Main02 {
    public static void main(String[] args) {

        // The factory for the event
        LongEventFactory factory = new LongEventFactory();

        //Specify the size of the ring buffer, must be power of 2.
        int bufferSize = 1024;

        //Construct the Disruptor
        Disruptor<LongEvent> disruptor = new Disruptor<>(factory, bufferSize, DaemonThreadFactory.INSTANCE);

        //Connect the handle
        disruptor.handleEventsWith(new LongEventHandle());

        //Start the Disruptor, starts all threads running
        disruptor.start();

        // Get the ring buffer from the Disruptor to be used for publishing
        RingBuffer<LongEvent> ringBuffer = disruptor.getRingBuffer();

        //=========================================================================为java8的写法做准备
        EventTranslator<LongEvent> translator1 = new EventTranslator<LongEvent>() {
            @Override
            public void translateTo(LongEvent longEvent, long sequence) {
                longEvent.set(8888L);
            }
        };
        ringBuffer.publishEvent(translator1);

        //=========================================================================为java8的写法做准备
        EventTranslatorOneArg<LongEvent,Long> translator2 = new EventTranslatorOneArg<LongEvent, Long>() {
            @Override
            public void translateTo(LongEvent longEvent, long sequence, Long value) {
                longEvent.set(value);
            }
        };
        ringBuffer.publishEvent(translator2, 7777L);

        //=========================================================================为java8的写法做准备
        EventTranslatorTwoArg<LongEvent,Long,Long> translator3 = new EventTranslatorTwoArg<LongEvent, Long, Long>() {
            @Override
            public void translateTo(LongEvent longEvent, long sequence, Long l1, Long l2) {
                longEvent.set(l1 + l2);
            }
        };
        ringBuffer.publishEvent(translator3, 10000L, 10000L);

        //=========================================================================为java8的写法做准备
        EventTranslatorThreeArg<LongEvent,Long,Long,Long> translator4 = new EventTranslatorThreeArg<LongEvent, Long, Long, Long>() {
            @Override
            public void translateTo(LongEvent longEvent, long sequence, Long l1, Long l2, Long l3) {
                longEvent.set(l1 + l2 + l3);
            }
        };
        ringBuffer.publishEvent(translator4, 10000L, 10000L, 10000L);

        //=========================================================================为java8的写法做准备
        EventTranslatorVararg<LongEvent> translator5 = new EventTranslatorVararg<LongEvent>() {
            @Override
            public void translateTo(LongEvent longEvent, long sequence, Object... objects) {
                long reust = 0;
                for (Object o : objects) {
                    long value = (long) o;
                    reust += value;
                }
                longEvent.set(reust);
            }
        };
        ringBuffer.publishEvent(translator5, 10000L, 10000L, 10000L,10000L,10000L);

    }
}
