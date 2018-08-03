package com.yzy.sell.repository;

import com.yzy.sell.dataobject.OrderMaster;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface OrderMasterRepository extends JpaRepository<OrderMaster, String> {

    Page<OrderMaster> findByBuyerOpenid(String buyerOpenid, Pageable pageable);

    Page<OrderMaster> findByOrderStatus(Integer orderStatus, Pageable pageable);

    List<OrderMaster> findByBuyerPhoneAndPayStatus(String buyerPhone,Integer payStatus);

}
