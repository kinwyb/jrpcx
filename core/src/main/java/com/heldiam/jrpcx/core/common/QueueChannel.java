package com.heldiam.jrpcx.core.common;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * 输出处理队列. 类似go的channel
 *
 * @author kinwyb
 * @date 2019-06-18 13:09
 **/
public class QueueChannel<T> {

    private final LinkedBlockingQueue<T> queue = new LinkedBlockingQueue<>();
    private static final Logger LOG = LoggerFactory.getLogger(QueueChannel.class);
    private boolean shutdown = false;
    private final EventLoopGroup group = new NioEventLoopGroup();

    /**
     * 系统处理队列
     *
     * @param run         处理方式
     * @param executeSize 处理线程数
     */
    public QueueChannel(QueueChannelRun<T> run, int executeSize) {
        for (int i = 0; i < executeSize; i++) {
            group.execute(new execute(this, run));
        }
    }

    /**
     * 系统处理队列
     */
    public QueueChannel() {
    }

    /**
     * 系统处理队列
     *
     * @param run 处理方式
     */
    public QueueChannel(QueueChannelRun<T> run) {
        group.execute(new execute(this, run));
    }


    public void Put(T data) throws InterruptedException {
        queue.put(data);
    }

    /**
     * 新增执行函数
     *
     * @param run
     */
    public void SubmitRun(QueueChannelRun<T> run) {
        group.execute(new execute(this, run));
    }

    public void Close() {
        LOG.debug("关闭任务线程池");
        shutdown = true;
        group.shutdownGracefully().awaitUninterruptibly();
        LOG.debug("成功关闭任务线程池");
    }

    /**
     * 执行接口
     *
     * @param <T>
     */
    public interface QueueChannelRun<T> {
        void Run(T data);
    }

    class execute<T> implements Runnable {

        private final QueueChannel<T> channel;
        private final QueueChannelRun run;

        execute(QueueChannel<T> channel, QueueChannelRun run) {
            this.channel = channel;
            this.run = run;
        }

        @Override
        public void run() {
            while (!channel.shutdown || channel.queue.size() > 0) {
                T result;
                try {
                    result = channel.queue.poll(1, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    LOG.debug("队列信息读取失败:" + e.getMessage());
                    continue;
                }
                if (result != null) {
                    try {
                        run.Run(result);
                    } catch (Exception e) {
                        LOG.error("任务处理异常", e);
                    }
                }
            }
        }

    }

}
