package com.yzy.sell.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yzy.sell.dataobject.OrderDetail;
import com.yzy.sell.dataobject.OrderMaster;
import com.yzy.sell.dataobject.SimuOrder;
import com.yzy.sell.dto.OrderDTO;
import com.yzy.sell.dto.SimuOrderDTO;
import com.yzy.sell.form.SimuForm;
import com.yzy.sell.repository.OrderMasterRepository;
import com.yzy.sell.repository.SimuOrderRepository;
import com.yzy.sell.service.BuyerService;
import com.yzy.sell.utils.ResultVOUtil;
import com.yzy.sell.viewobject.ResultVO;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @description: 私幕订单管理
 * @author: yzy
 * @create: 2018-05-19 00:41
 */
@RestController
@RequestMapping("/simu/")
@Slf4j
public class SimuController {
    @Autowired
    private OrderMasterRepository repository;
    @Autowired
    private BuyerService buyerService;
    @Autowired
    private SimuOrderRepository simuRepository;

    private List<SimuOrderDTO> simuOrderDTOList;
    @GetMapping("/list")
    public ResultVO<List<SimuOrderDTO>> list(@RequestParam("phone") String phone){
        simuOrderDTOList=new ArrayList<>();
        List<OrderMaster> list=repository.findByBuyerPhoneAndPayStatus(phone,1);
        for(OrderMaster order:list){
            OrderDTO orderDTO = buyerService.findOrderOne(order.getBuyerOpenid(), order.getOrderId());
            List<OrderDetail> orderDetailList = orderDTO.getOrderDetailList();
            for(OrderDetail detail:orderDetailList){
                if(detail.getProductName().toLowerCase().contains("shuke")){
                    Optional<SimuOrder> byId = simuRepository.findById(detail.getDetailId());
                    SimuOrder simuOrder=byId.isPresent()?byId.get():null;
                    if(simuOrder==null) {
                        SimuOrderDTO simuOrderDTO = new SimuOrderDTO();
                        BeanUtils.copyProperties(detail,simuOrderDTO);
                        simuOrderDTO.setBuyerName(order.getBuyerName());
                        simuOrderDTO.setBuyerPhone(order.getBuyerPhone());
                        simuOrderDTOList.add(simuOrderDTO);
                    }
                    else if(detail.getProductQuantity()-simuOrder.getProductQuantity()>0){
                        SimuOrderDTO simuOrderDTO = new SimuOrderDTO();
                        BeanUtils.copyProperties(detail,simuOrderDTO);
                        simuOrderDTO.setProductQuantity(detail.getProductQuantity()-simuOrder.getProductQuantity());
                        simuOrderDTO.setBuyerName(order.getBuyerName());
                        simuOrderDTO.setBuyerPhone(order.getBuyerPhone());
                        simuOrderDTOList.add(simuOrderDTO);
                    }
                }
            }

        }
        return ResultVOUtil.success(simuOrderDTOList);
    }
    @RequestMapping("/save")
    public ResultVO save(@RequestParam("orderList") String str) {
        Gson gson=new Gson();
        List<SimuOrderDTO> orderDetailList = gson.fromJson(str, new TypeToken<List<SimuOrderDTO>>() {
        }.getType());
        log.info("服务器--"+orderDetailList.size());
        for(SimuOrderDTO simuOrderDTO:orderDetailList) {
            Optional<SimuOrder> byId = simuRepository.findById(simuOrderDTO.getDetailId());
            SimuOrder simuOrder = byId.isPresent() ? byId.get() : null;
            if (simuOrder == null) {
                simuOrder = new SimuOrder();
                BeanUtils.copyProperties(simuOrderDTO, simuOrder);
                simuOrder.setProductQuantity(1);
                SimuOrder result = simuRepository.save(simuOrder);
                if (result == null) return ResultVOUtil.error(-1, "订单号:"+simuOrderDTO.getDetailId()+"归档失败");
            } else {
                simuOrder.setProductQuantity(simuOrder.getProductQuantity() + 1);
                SimuOrder result = simuRepository.save(simuOrder);
                if (result == null) return ResultVOUtil.error(-1, "订单号:"+simuOrderDTO.getDetailId()+"归档失败");
            }
        }
        return ResultVOUtil.success();
    }
}
