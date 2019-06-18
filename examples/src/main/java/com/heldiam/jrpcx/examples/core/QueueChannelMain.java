package com.heldiam.jrpcx.examples.core;

import com.heldiam.jrpcx.core.common.QueueChannel;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author kinwyb
 * @date 2019-06-18 13:36
 **/
public class QueueChannelMain {

    public static void main(String[] args) {
        QueueChannel<String> channel = new QueueChannel<>();
        AtomicInteger integer = new AtomicInteger();
        channel.SubmitRun((r) -> {
            System.out.println("Thread 1 => " + r);
            integer.incrementAndGet();
        });
        channel.SubmitRun((r) -> {
            System.out.println("Thread 2 => " + r);
            integer.incrementAndGet();

        });
        channel.SubmitRun((r) -> {
            System.out.println("Thread 3 => " + r);
            integer.incrementAndGet();
        });
        channel.SubmitRun((r) -> {
            System.out.println("Thread 4 => " + r);
            integer.incrementAndGet();
        });
        channel.SubmitRun((r) -> {
            System.out.println("Thread 5 => " + r);
            integer.incrementAndGet();
        });
        channel.SubmitRun((r) -> {
            System.out.println("Thread 6 => " + r);
            integer.incrementAndGet();
        });
        for (int i = 0; i < 1000; i++) {
            try {
                channel.Put("Index =>" + i);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        channel.Close();
        System.out.println("任务执行完成.. " + integer.toString());
    }
}
