package co.axelrod.lmax;

import co.axelrod.lmax.event.LongEvent;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.util.DaemonThreadFactory;

import java.nio.ByteBuffer;

import static co.axelrod.lmax.util.MemoryUtils.printMemoryUsage;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        printMemoryUsage();

        int bufferSize = 1024;

        Disruptor<LongEvent> disruptor = new Disruptor<>(LongEvent::new, bufferSize, DaemonThreadFactory.INSTANCE);

        disruptor.handleEventsWith((event, sequence, endOfBatch) ->
                System.out.println("Event: " + event));
        disruptor.start();


        RingBuffer<LongEvent> ringBuffer = disruptor.getRingBuffer();
        ByteBuffer bb = ByteBuffer.allocate(8);
        for (long l = 0; true; l++) {
            bb.putLong(0, l);
            ringBuffer.publishEvent((event, sequence, buffer) -> event.set(buffer.getLong(0)), bb);
            System.out.println("Event published: " + l);
            printMemoryUsage();
            Thread.sleep(1000);
        }
    }

    public static void allocateMemoryInCycle() throws InterruptedException {
        int size = 10_000_000;

        for (int i = 0; i < 1000; i++) {
            byte[] array = new byte[size];
            printMemoryUsage();
            Thread.sleep(1000L);
            System.out.println("Iteration: " + i + ", allocated " + (i + 1) * size / 1000000 + " MB");
        }

        System.out.println("Done");
    }
}