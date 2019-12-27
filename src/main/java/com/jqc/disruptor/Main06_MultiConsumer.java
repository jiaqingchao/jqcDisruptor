package com.jqc.disruptor;

import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.SleepingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

import java.io.IOException;
import java.util.concurrent.*;

public class Main06_MultiConsumer {
    public static void main(String[] args) throws IOException, InterruptedException {

        // The factory for the event
        LongEventFactory factory = new LongEventFactory();

        //Specify the size of the ring buffer, must be power of 2.
        int bufferSize = 1024;

        //指定单线程模式-未加锁，默认多线程-加锁
        Disruptor<LongEvent> disruptor = new Disruptor<>(factory, bufferSize, Executors.defaultThreadFactory(), ProducerType.MULTI, new SleepingWaitStrategy());

        //Connect the handles
        LongEventHandle h1 = new LongEventHandle();
        LongEventHandle h2 = new LongEventHandle();
        disruptor.handleEventsWith(h1, h2);

        //Start the Disruptor, starts all threads running
        disruptor.start();

        // Get the ring buffer from the Disruptor to be used for publishing
        RingBuffer<LongEvent> ringBuffer = disruptor.getRingBuffer();

        final int threadCount = 10;
        CyclicBarrier barrier = new CyclicBarrier(threadCount);
        ExecutorService service = Executors.newCachedThreadPool();
        for (long i = 0; i < threadCount; i++) {
            final long threadNum = i;
            service.submit(() -> {
                System.out.println("Thread" + threadNum + "ready to start!");
                try {
                    barrier.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                }

                for (int j = 0; j < 100; j++) {
                    int finalJ = j;
                    ringBuffer.publishEvent((longEvent, sequence) -> {
                        longEvent.set(threadNum);
                        System.out.println("生产了 " + threadNum + finalJ);
                    });
                }
            });
        }

        service.shutdown();
//            disruptor.shutdown();
        TimeUnit.SECONDS.sleep(3);
        System.out.println(LongEventHandle.count);
    }
}
