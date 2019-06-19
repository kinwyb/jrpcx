# jrpcx
rpcx的原生java端 

实现编码
> * msgpack (rpcx默认编码)
> * json

实现功能
> * 基本调用(TCP)
> * 服务注册发现(etcdv2)
> * 服务超时
> * 失败重试
> * 支持元数据(使用方法结果类继承MetaData)


#使用方法
定义服务接口
```
/**
 * 测试接口
 *
 * @author heldiam
 */
public interface Arith {

    /**
     * 乘法
     *
     * @param request 请求参数
     * @return
     */
    ArithMulResponse Mul(ArithMulRequest request);

}
```
请求结构
```
/**
 *
 * @author heldiam
 */
public class ArithMulRequest {

    private Integer A;
    private Integer B;

    public Integer getA() {
        return A;
    }

    public void setA(Integer A) {
        this.A = A;
    }

    public Integer getB() {
        return B;
    }

    public void setB(Integer B) {
        this.B = B;
    }

}
```

返回结果结构
```
/**
 * 返回结果
 * @author heldiam
 */
public class ArithMulResponse implements Serializable{
    private Integer C;

    public Integer getC() {
        return C;
    }

    public void setC(Integer C) {
        this.C = C;
    }
    
}
```
### 服务端
服务路径只取类名不包含包路径
```
/**
 * @author kinwyb
 * @date 2019-06-15 09:07
 **/
public class ServerMain {

    public static void main(String[] args) throws Exception {
        BasicConfigurator.configure();
        Arith arith = request -> {
            ArithMulResponse response = new ArithMulResponse();
            response.setC(request.getA() * request.getB());
            return response;
        };
        Service.registerService(arith, Arith.class);
        EtcdV2Discovery discovery = new EtcdV2Discovery("118.31.188.131:2379");
        discovery.setBasePath("/jrpcx");
        Server server = new Server("127.0.0.1:8972", discovery);
        new Thread(() -> server.Start()).run();
        System.out.println("定时关闭服务");
        Thread.sleep(30000);
        server.Close();
    }
}
```

### 客户端
⚠️ 注意：执行是异步的,直接读取结果属性可能为空。必须通过get方法来获取结果.通过get方法获取该方法会一直阻塞到服务端返回结果
```
/**
 * @author kinwyb
 * @date 2019-06-14 17:38
 **/
public class ClientMain {

    public static void main(String[] args) throws Exception {
        BasicConfigurator.configure();
        EtcdV2Discovery discovery = new EtcdV2Discovery("118.31.188.131:2379");
        discovery.setBasePath("/jrpcx");
        Client client = Selector.WeightedRountRobin(discovery).buildClient();
        Arith arith = client.Proxy(Arith.class);
        ArithMulRequest req = new ArithMulRequest();
        req.setA(7);
        req.setB(8);
        for (int i = 0; i < 1000; i++) {
            ArithMulResponse rep = arith.Mul(req);
            System.out.println("结果:" + rep.getC());
            Thread.sleep(1000);
        }
        client.Close();
    }

}
```