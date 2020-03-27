package com.xuzhou.vertx.verticle;


import com.alibaba.fastjson.JSONObject;
import com.xuzhou.vertx.controller.DeviceController;
import com.xuzhou.vertx.entity.Result;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Simple web server verticle to expose the results of the Spring service bean call (routed via a verticle - see
 * SpringDemoVerticle)
 */
public class HttpServerVerticle extends AbstractVerticle {
    private final Logger logger = LoggerFactory.getLogger(HttpServerVerticle.class);
    private DeviceController deviceController = new DeviceController();

    protected Router router;
    HttpServer server;
    private int port;

    public HttpServerVerticle(int port) {
        this.port = port;
    }

    @Override
    public void start(Future<Void> future) {
        logger.info("==============web start==============");
        server = vertx.createHttpServer(createOptions());
        server.requestHandler(createRouter());
        server.listen(result2 -> {
            if (result2.succeeded()) {
                future.complete();
            } else {
                future.fail(result2.cause());
            }
        });
    }

    @Override
    public void stop(Future<Void> future) {
        logger.info("==============web stop==============");
        if (server == null) {
            future.complete();
            return;
        }
        server.close(result -> {
            if (result.failed()) {
                future.fail(result.cause());
            } else {
                future.complete();
            }
        });
    }


    private HttpServerOptions createOptions() {
        HttpServerOptions options = new HttpServerOptions();
        options.setPort(port);
        return options;
    }

    private Router createRouter() {
        router = Router.router(vertx);
        router.route().handler(ctx -> {
            logger.debug("path:" + ctx.request().path() + "----uri:" + ctx.request().absoluteURI() + "-------method:" + ctx.request().method());
            ctx.response().headers().add(HttpHeaders.CONTENT_TYPE, "application/json; charset=utf-8");
            ctx.next();
        });


        router.route().handler(CorsHandler.create("*")
                .allowedMethod(HttpMethod.GET)
                .allowedMethod(HttpMethod.POST)
                .allowedMethod(HttpMethod.OPTIONS)
                .allowedMethod(HttpMethod.HEAD)
                .allowedMethod(HttpMethod.PUT)
                .allowedMethod(HttpMethod.DELETE)
                .allowedHeader("X-PINGOTHER")
                .allowedHeader("Origin")
                .allowedHeader("Content-Type")
                .allowedHeader("Accept")
                .allowedHeader("X-Requested-With")
                .maxAgeSeconds(1728000)
        );
        try {
            router.route().handler(BodyHandler.create());
            //捕获全局的异常
            router.route().failureHandler(route -> {
                logger.error("Routing error:{}", route.failure());
                Result result = new Result<>(500, "服务器异常");
                route.response().end(JSONObject.toJSONString(result));
            });
            router.route("/device/*").handler(this::Authentication);
            //首页
            router.route("/device/index").handler(deviceController::index);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return router;
    }

    /**
     * 鉴权方法
     *
     * @param event
     */
    public void Authentication(RoutingContext event) {
        event.next();
    }
}
