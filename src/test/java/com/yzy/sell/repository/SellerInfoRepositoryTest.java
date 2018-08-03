package com.yzy.sell.repository;

import com.yzy.sell.dataobject.SellerInfo;
import com.yzy.sell.dataobject.SimuOrder;
import com.yzy.sell.utils.KeyUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SellerInfoRepositoryTest {
    @Autowired
    private SimuOrderRepository repository;

    @Test
    public void findById() {
        SimuOrder result=repository.findById("abc").get();
        Assert.assertNotNull(result);
    }

    @Test
    public void save() {
        SimuOrder simuOrder=new SimuOrder();
        simuOrder.setDetailId("123");
        simuOrder.setProductQuantity(1);
        simuOrder.setBuyerPhone("1222222222");
        simuOrder.setBuyerName("name");

        SimuOrder result = repository.save(simuOrder);
        Assert.assertNotEquals(result,null);
    }
}