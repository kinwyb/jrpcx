package com.heldiam.jrpcx.benchmark.client;

import com.heldiam.jrpcx.client.Client;
import com.heldiam.jrpcx.client.selector.Selector;
import com.heldiam.jrpcx.core.discovery.SimpleDiscovery;
import com.heldiam.jrpcx.examples.exampleData.Arith;
import com.heldiam.jrpcx.examples.exampleData.ArithMulRequest;
import com.heldiam.jrpcx.examples.exampleData.ArithMulResponse;
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
@BenchmarkMode(Mode.All)
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Benchmark)
@Fork(2)
public class ThroughputBenchmark {

    private Client client;
    private Arith arith;
    private ArithMulRequest req = new ArithMulRequest();

    @Setup
    public void init() {
        client = Selector.RoundRobin(new SimpleDiscovery("127.0.0.1:8972")).buildClient();
        arith = client.Proxy(Arith.class);
        req.setA(19);
        req.setB(29);
    }

    @TearDown
    public void stop() {
        client.Close();
    }

    @Benchmark
    public void Mark() {
        ArithMulResponse response = arith.Mul(req);
        response.getC();
    }

    public static void main(String[] args) throws RunnerException {
        Options options = new OptionsBuilder().include(ThroughputBenchmark.class.getSimpleName()).build();
        new Runner(options).run();
    }

}
