package com.yzy.sell.handler;

import com.yzy.sell.Excetion.SellException;
import com.yzy.sell.Excetion.SellerAuthorizeException;
import com.yzy.sell.utils.ResultVOUtil;
import com.yzy.sell.viewobject.ResultVO;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/**
 * @description:
 * @author: yzy
 * @create: 2018-04-02 11:59
 */
@ControllerAdvice
public class SellExceptionHandler {
    @ExceptionHandler({SellerAuthorizeException.class})
    public ModelAndView handlerAuthorizeException(){
        return new ModelAndView("redirect:/index.html");
        //TODO 登录页面地址

    }

//    @ExceptionHandler({SellException.class})
//    @ResponseBody
//    public ResultVO handleSellException(SellException e){
//        return ResultVOUtil.error(e.getCode(),e.getMessage());
//    }
}
