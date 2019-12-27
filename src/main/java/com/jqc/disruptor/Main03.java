package com.jqc.disruptor;

import com.lmax.disruptor.*;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.util.DaemonThreadFactory;

import java.io.IOException;

public class Main03 {
    public static void main(String[] args) throws IOException {

        //Specify the size of the ring buffer, must be power of 2.
        int bufferSize = 1024;

        //Construct the Disruptor
        Disruptor<LongEvent> disruptor = new Disruptor<>(LongEvent::new, bufferSize, DaemonThreadFactory.INSTANCE);

        //Connect the handle
        disruptor.handleEventsWith(
                (longEvent, sequence, endOfBatch) -> System.out.printf("[" + Thread.currentThread().getName() + "]" + longEvent + "，序号：" + sequence)
        );

        //Start the Disruptor, starts all threads running
        disruptor.start();

        // Get the ring buffer from the Disruptor to be used for publishing
        RingBuffer<LongEvent> ringBuffer = disruptor.getRingBuffer();

        ringBuffer.publishEvent((longEvent, sequence) -> longEvent.set(8888L));
        ringBuffer.publishEvent((longEvent, sequence, l) -> longEvent.set(l), 7777L);
        ringBuffer.publishEvent((longEvent, sequence, l1, l2) -> longEvent.set(l1 + l2), 10000L, 10000L);
        ringBuffer.publishEvent((longEvent, sequence, l1, l2, l3) -> longEvent.set(l1 + l2 + l3), 10000L, 10000L, 10000L);
        ringBuffer.publishEvent((longEvent, sequence, objects) -> {
            long reust = 0;
            for (Object o : objects) {
                long value = (long) o;
                reust += value;
            }
            longEvent.set(reust);
        }, 10000L, 10000L, 10000L, 10000L, 10000L);

        System.in.read();

    }
}
