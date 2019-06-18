package com.heldiam.jrpcx.core.common;

import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * @author kinwyb
 * @date 2019-06-14 16:18
 */
public class Constants {

    public static final String VERSION_KEY = "version";

    public static final String GROUP_KEY = "group";

    public static final String BACKUP_KEY = "backup";

    public static final String DEFAULT_KEY_PREFIX = "default.";

    public static final Pattern COMMA_SPLIT_PATTERN = Pattern.compile("\\s*[,]+\\s*");

    public static final String INTERFACE_KEY = "interface";

    public static final String LOCALHOST_KEY = "localhost";

    public static final String ANYHOST_VALUE = "0.0.0.0";

    public static final String ANYHOST_KEY = "anyhost";

    public static final String ASYNC_KEY = "async";

    public static final String SYNC_KEY = "sync";

    public static final String ONE_WAY_KEY = "oneway";

    public static final String RETURN_KEY = "return";

    public static final String $ECHO = "$echo";

    public static final String $HOT_DEPLOY = "$hot_deploy";

    public static final String TOKEN_KEY = "token";

    public static final String TIMEOUT_KEY = "timeout";

    public static final String PATH_KEY = "path";

    public static final String CACHE_KEY = "cache";

    public static final String ACCESSLOG_KEY = "accesslog";

    public static final String FALSE = "false";

    public static final String TRUE = "true";

    public static final String COMMA_SEPARATOR = ",";

    public static final String TPS_LIMIT_RATE_KEY = "tps";

    public static final String TPS_LIMIT_INTERVAL_KEY = "tps.interval";

    public static final long DEFAULT_TPS_LIMIT_INTERVAL = TimeUnit.MINUTES.toMillis(1);

    public static final String MONITOR_KEY = "monitor";

    public static final String APPLICATION_KEY = "application";

    public static final String INPUT_KEY = "input";

    public static final String OUTPUT_KEY = "output";

    public static final String SIDE_KEY = "side";

    public static final String PROVIDER_SIDE = "provider";

    public static final String CONSUMER_SIDE = "consumer";

    public static final String COUNT_PROTOCOL = "count";

    public static final String PROVIDER = "provider";

    public static final String CONSUMER = "consumer";

    public static final String $INVOKE = "$invoke";

    public static final String SEND_TYPE = "sendType";

    public static final String FAIL_TYPE = "fail.type";

    public static final String LANGUAGE = "language";

    public static final String RPCX_ERROR_CODE = "__rpcx_error__";

    public static final String RPCX_ERROR_MESSAGE = "_rpcx_error_message";

    public static final String TRACE_ID = "trace_id";

    public static final String SPAN_ID = "span_id";

    public static final String X_RPCX_SERVICEPATH = "X-RPCX-ServicePath";

    public static final String X_RPCX_SERVICEMETHOD = "X-RPCX-ServiceMethod";

    public static final String X_RPCX_TRACEID = "X-RPCX-TraceId";

    public static final String CharsetName = "UTF-8";

}