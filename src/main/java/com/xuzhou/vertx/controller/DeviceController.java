package com.xuzhou.vertx.controller;


import com.alibaba.fastjson.JSONObject;
import com.xuzhou.vertx.entity.Result;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by IntelliJ IDEA.
 * User: xuzhou
 * Date: 2018/12/8
 * Time: 13:41
 */
public class DeviceController {
    private static Logger logger = LoggerFactory.getLogger(DeviceController.class);
    private ExecutorService consumerExecutor = new ThreadPoolExecutor(15, 20, 5, TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(512), // 使用有界队列，避免OOM
            new ThreadPoolExecutor.DiscardPolicy());


    /**
     * 首次开机登陆
     * 上报 设备号、UID、PID、版本、固件编译时间、本地局域网IP、连接的wifi名称
     *
     * @param context
     */
    public void index(RoutingContext context) {
        HttpServerRequest request = context.request();
        try {
            String name = request.getParam("name");
            int age = Integer.parseInt(request.getParam("age"));

            Result result = new Result<>(new JSONObject() {{
                put("name", name);
                put("age", age);
            }});
            request.response().end(JSONObject.toJSONString(result));
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }


}
