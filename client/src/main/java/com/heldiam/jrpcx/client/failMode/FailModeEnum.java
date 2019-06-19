package com.heldiam.jrpcx.client.failMode;

/**
 * 网络错误或者服务超时重试模式
 */
public enum FailModeEnum {

    /**
     * 一旦调用一个节点失败， 立即会返回错误
     */
    Failfast,
    /**
     * 失败后调用另外一个节点,直到服务节点能正常返回信息,或者达到最大的重试次数
     * 最大次数设置 Feature.MaxRetryNUM
     */
    Failover,
    /**
     * 失败后重新调用,但有可能还是会选择当前节点，直到返回正常信息或达到最大重试次数
     */
    Failtry,
    /**
     * 失败后会将请求发送到另外一个节点,两个节点任何一个返回就算调用成功
     */
    Failbackup;

    private static final IFailMode failFast = new Failfast();
    private static final IFailMode failover = new FailOver();
    private static final IFailMode failtry = new FailTry();
    private static final IFailMode failbackup = new FailBackup();

    /**
     * 获取调用模式
     *
     * @return
     */
    public IFailMode getMode() {
        switch (this) {
            case Failfast:
                return failFast;
            case Failover:
                return failover;
            case Failbackup:
                return failbackup;
            default:
                return failtry;
        }
    }

}
