package com.cornucopib.websocketdemo.component;

import com.sun.org.slf4j.internal.LoggerFactory;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * WebSocket服务.
 *
 * @author cornucopib
 * @since 2022/1/15
 */
@ServerEndpoint("/webSocket/{sid}")
@Component
@Slf4j
public class WebSocketServer {

    // 记录在线连接数
    private static ConcurrentHashMap<String, Boolean> onLineInfo = new ConcurrentHashMap<>();
    // session池
    private static ConcurrentHashMap<String, Session> sessionPools = new ConcurrentHashMap<>();

    public static final String ON_LINE = "on_line";
    public static final String OFF_LINE = "off_line";
    public static final String TOTAL = "total";

    @OnOpen
    public void onOpen(Session session, @PathParam(value = "sid") String sid) {
        onLineInfo.put(sid, true);
        sessionPools.put(sid, session);
        log.info("New connection: {} joined,The number of people currently online is {}", sid, calculateNumberOfPeopleOnline());
    }

    @OnClose
    public void onClose(@PathParam(value = "sid") String sid) {
        onLineInfo.put(sid, false);
        sessionPools.remove(sid);
        log.info("The connection: {} closed,The number of people currently online is {}", sid, calculateNumberOfPeopleOnline());
    }

    @OnMessage
    public void onMessage(String message) {

    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        log.error("the user:{} connect error,error info:{}",session.getUserPrincipal().getName(),throwable.getMessage());
    }

    /**
     * 计算在线人数.
     *
     * @return 在线人数
     */
    private Integer calculateNumberOfPeopleOnline() {
        return calculateNumberOfPeople(CalculationType.ON_LINE);
    }

    /**
     * 计算离线人数.
     *
     * @return 离线人数
     */
    private Integer calculateNumberOfPeopleOffline() {
        return calculateNumberOfPeople(CalculationType.OFF_LINE);
    }

    /**
     * 计算访问人数.
     *
     * @return 访问人数
     */
    private Integer calculateNumberOfPeopleTotal() {
        return calculateNumberOfPeople(CalculationType.TOTAL);
    }


    /**
     * 计算人数
     *
     * @param calculationType ON_LINE，计算在线;OFF_LINE,计算离线;TOTAL,计算访问人数
     * @return 人数
     */
    private Integer calculateNumberOfPeople(CalculationType calculationType) {
        AtomicInteger numOfPeople = new AtomicInteger();
        if (onLineInfo != null) {
            Iterator<Map.Entry<String, Boolean>> iterator = onLineInfo.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, Boolean> next = iterator.next();
                Boolean isOnLine = next.getValue();
                if (CalculationType.ON_LINE.equals(calculationType)) {
                    if (isOnLine) {
                        numOfPeople.getAndIncrement();
                    }
                } else if (CalculationType.OFF_LINE.equals(calculationType)) {
                    if (!isOnLine) {
                        numOfPeople.getAndIncrement();
                    }
                } else if (CalculationType.TOTAL.equals(calculationType)) {
                    numOfPeople.getAndIncrement();
                }
            }
        }
        return numOfPeople.get();
    }

    private enum CalculationType {

        ON_LINE("on_line"),
        OFF_LINE("off_line"),
        TOTAL("total");

        private final String type;

        private CalculationType(String type) {
            this.type = type;
        }
    }


}
