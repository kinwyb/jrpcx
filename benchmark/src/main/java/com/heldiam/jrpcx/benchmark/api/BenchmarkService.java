package com.heldiam.jrpcx.benchmark.api;

public interface BenchmarkService {
    BenchmarkResp say(BenchmarkReq msg);

    void sayNoBack(BenchmarkReq msg);

    String sayString(String msg);

    byte[] sayByte(byte[] data);
}
