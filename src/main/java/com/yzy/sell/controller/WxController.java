package com.yzy.sell.controller;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.impl.WxMaServiceImpl;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.yzy.sell.Excetion.SellException;
import com.yzy.sell.config.WxConfig;
import com.yzy.sell.constant.TokenConstant;
import com.yzy.sell.dataobject.UserInfo;
import com.yzy.sell.enums.ResultEnum;
import com.yzy.sell.form.UserForm;
import com.yzy.sell.repository.UserInfoRepository;
import com.yzy.sell.service.UserService;
import com.yzy.sell.utils.ResultVOUtil;
import com.yzy.sell.viewobject.ResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @description: 微信
 * @author: yzy
 * @create: 2018-04-03 12:48
 */
@RestController
@RequestMapping("/wechat")
@Slf4j
public class WxController {
    @Autowired
    private WxConfig wxConfig;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private UserInfoRepository repository;
    /**
     * 拿openid并存入redis
     * @param:
     */
    @PostMapping("/auth")
    public ResultVO<Map<String,String>> auth(@RequestParam("code") String code,@RequestParam("nickName") String nickName){
        log.info("code={},nickName={}",code,nickName);
        String url="https://api.weixin.qq.com/sns/jscode2session?appid="+wxConfig.getAppId()+"&secret="+wxConfig.getAppSecret()+"&js_code="+code+"&grant_type=authorization_code";
        RestTemplate restTemplate=new RestTemplate();
        String response=restTemplate.getForObject(url,String.class);
        JsonObject json = new JsonParser().parse(response).getAsJsonObject();
        log.info("user--"+json);
        String openid=json.get("openid").getAsString();
        String token = UUID.randomUUID().toString();
        Integer expire = TokenConstant.EXPIRE;
        redisTemplate.opsForValue().set(String.format(TokenConstant.TOKEN_PREFIX,token),openid, expire, TimeUnit.SECONDS);
        Map<String,String> map=new HashMap<>();
        map.put("token",token);

        //将用户openid存入数据库
        UserInfo userInfo=repository.findByOpenid(openid);
        if(userInfo==null) {
            log.info("新用户={}",openid);
            UserInfo newUser = new UserInfo();
            newUser.setOpenid(openid);
            newUser.setNickName(nickName);
            repository.save(newUser);
        }else{
            log.info("老用户={}，nickName={}",openid,nickName);
            userInfo.setNickName(nickName);
            repository.save(userInfo);
        }

        return ResultVOUtil.success(map);
    }
    //客户端用token登录，后台通过token查openid

    @GetMapping("/login")
    public ResultVO<Map<String,String>> login(@RequestParam("token") String token){
        log.info("token={}",token);
        String key=String.format(TokenConstant.TOKEN_PREFIX,token);
        if(redisTemplate.opsForValue().get(key)==null) return ResultVOUtil.error(-1,"relogin");
        else return ResultVOUtil.success();
    }

}
