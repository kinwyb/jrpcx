package com.heldiam.jrpcx.benchmark.server;

import com.heldiam.jrpcx.benchmark.api.BenchmarkReq;
import com.heldiam.jrpcx.benchmark.api.BenchmarkResp;
import com.heldiam.jrpcx.benchmark.api.BenchmarkService;
import com.heldiam.jrpcx.core.discovery.SimpleDiscovery;
import com.heldiam.jrpcx.server.Server;
import com.heldiam.jrpcx.server.Service;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.UnsupportedEncodingException;

/**
 * @author kinwyb
 * @date 2019-06-20 11:23
 **/
public class server {

    public static void main(String[] args) {
        BasicConfigurator.configure();
        Logger root = Logger.getRootLogger();
        root.setLevel(Level.WARN);
        resp.setWords("ok");
        BenchmarkService benchmarkService = new BenchmarkServiceImpl();
        Service.registerService(benchmarkService, BenchmarkService.class);
        Server server = new Server("127.0.0.1:8972", new SimpleDiscovery());
//        new Thread(() -> server.Start()).run();
        server.Start();
    }

    private static final BenchmarkResp resp = new BenchmarkResp();

    static class BenchmarkServiceImpl implements BenchmarkService {

        @Override
        public BenchmarkResp say(BenchmarkReq msg) {
            return resp;
        }

        @Override
        public void sayNoBack(BenchmarkReq msg) {
        }

        @Override
        public String sayString(String msg) {
            return "字符串";
        }

        @Override
        public byte[] sayByte(byte[] data) {
            try {
                return "haha".getBytes("UTF-8");
            } catch (UnsupportedEncodingException e) {
                return new byte[]{0x1};
            }
        }
    }

}
