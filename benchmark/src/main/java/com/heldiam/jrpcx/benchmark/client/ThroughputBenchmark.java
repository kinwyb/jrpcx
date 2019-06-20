package com.heldiam.jrpcx.benchmark.client;

import com.heldiam.jrpcx.benchmark.api.BenchmarkReq;
import com.heldiam.jrpcx.benchmark.api.BenchmarkService;
import com.heldiam.jrpcx.client.Client;
import com.heldiam.jrpcx.client.selector.Selector;
import com.heldiam.jrpcx.core.discovery.SimpleDiscovery;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

/**
 * 整体吞吐量测试
 *
 * @author kinwyb
 * @date 2019-06-19 16:28
 **/
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
public class ThroughputBenchmark {

    private Client client;
    private BenchmarkService service;
    private BenchmarkReq req = new BenchmarkReq();

    @Setup
    public void init() {
        client = Selector.RoundRobin(new SimpleDiscovery("127.0.0.1:8972")).buildClient();
        service = client.Proxy(BenchmarkService.class);
        req.setWords("heldiam");
    }

    @TearDown
    public void stop() {
        client.Close();
    }

    @Benchmark
    public void Mark() {
        service.say(req).getWords();
    }

    public static void main(String[] args) throws RunnerException {
        Options options = new OptionsBuilder().include(ThroughputBenchmark.class.getSimpleName()).build();
        new Runner(options).run();
    }

}
