package com.github.hzredwall.rest;

import com.github.hzredwall.common.BaseCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
* 示例-cache
* 每个ip 每小时只能请求10次
* @author RedWall
* @mail walkmanlucas@gmail.com
* @date 2018/11/15
* @since JDK 1.8
**/
@Slf4j
@Controller
@RequestMapping(value = "order")
public class OrderController {

    @Autowired
    HttpServletRequest request;

    private static BaseCache<String,Long> cache = new BaseCache(1, TimeUnit.HOURS) {
        @Override
        protected Object loadData(Object o) {
            return 0L;
        }
    };

    @RequestMapping(value = "add")
    @ResponseBody
    public ResponseEntity order(){
        log.info("start take order...");
        String ipAddress = getIpAddress(request);
        Long count = Optional.ofNullable(cache.getCache(ipAddress)).map(tmp -> tmp).orElse(0L);
        Boolean can = count > 9 ? false : true;
        if (can){
            log.info("ip is less than 10 in 1 hour...");
            cache.put(ipAddress,count + 1L);
            return ResponseEntity.ok("go on...");
        }
        cache.put(ipAddress,count + 1L);
        log.error("ip:" + ipAddress + "-count:" + count);
        log.error("ip is more than 10 in 1 hour...");
        return ResponseEntity.status(500).body("ip is not accepted...");
    }



    public static String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }




}
