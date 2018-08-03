package com.yzy.sell.controller;

import com.yzy.sell.constant.CookieConstant;
import com.yzy.sell.constant.TokenConstant;
import com.yzy.sell.dataobject.SellerInfo;
import com.yzy.sell.enums.ResultEnum;
import com.yzy.sell.service.SellerService;
import com.yzy.sell.utils.CookieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @description: 卖家用户
 * @author: yzy
 * @create: 2018-04-01 18:45
 */
@Controller
@RequestMapping("/seller")
public class SellerUserController {
    @Autowired
    private SellerService sellerService;

    @Autowired
    private StringRedisTemplate redisTemplate;


    @RequestMapping("/login")
    //TODO 修改登录验证 POST方式，小程序端用openid登录
    public ModelAndView login(@RequestParam("openid") String openid, Map<String,Object> map, HttpServletResponse resp){

        SellerInfo sellerInfo=sellerService.findSellerInfoByOpenid(openid);
        if(sellerInfo==null){
            map.put("msg", ResultEnum.LOGIN_FAIL.getMessage());
            map.put("url","/sell/seller/order/list");
            return new ModelAndView("/common/error",map);
        }

        //设置TOKEN至Redis
        String token = UUID.randomUUID().toString();
        Integer expire = TokenConstant.EXPIRE*6;
        redisTemplate.opsForValue().set(String.format(TokenConstant.TOKEN_PREFIX,token),openid, expire,TimeUnit.SECONDS);

        //设置TOKEN至cookie (不保存cookie)
        CookieUtil.set(resp, CookieConstant.TOKEN,token,-1);

        return new ModelAndView("redirect:/seller/order/list");
    }

    @GetMapping("/logout")
    public ModelAndView logout(HttpServletResponse resp, HttpServletRequest req,Map<String,Object> map){
        //从Cookie中查询
        Cookie cookie = CookieUtil.get(req, CookieConstant.TOKEN);
        if(cookie!=null) {
            //清除redis
            redisTemplate.opsForValue().getOperations().delete(String.format(TokenConstant.TOKEN_PREFIX,cookie.getValue()));
            //清除Cookie
            CookieUtil.set(resp,CookieConstant.TOKEN,null,0);
        }
        map.put("msg",ResultEnum.LOGOUT_SUCCESS.getMessage());
        map.put("url","/sell/seller/order/list");
        return new ModelAndView("common/success",map);
    }

}
