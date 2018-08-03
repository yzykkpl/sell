<html>
<head>
    <meta charset="utf-8">
    <title>塬仓-订单列表</title>
    <link href="https://cdn.bootcss.com/bootstrap/3.3.7/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.bootcss.com/jquery-impromptu/6.2.3/jquery-impromptu.min.css" rel="stylesheet">
    <script src="https://cdn.bootcss.com/jquery/3.0.0/jquery.min.js"></script>
    <script src="https://cdn.bootcss.com/jquery-impromptu/6.2.3/jquery-impromptu.min.js"></script>
    <script src="https://cdn.bootcss.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>


    <link href="/sell/css/style.css" rel="stylesheet">
</head>
<body>
    <#assign total = orderDTOPage.getTotalPages()>
<div id="wrapper" class="toggled">
<#--边栏-->
            <#include "../common/nav.ftl">
<#--内容区-->

    <div id="page-content-wrapper">
        <div class="container-fluid">
            <div class="row clearfix">
                <div class="col-md-12 column">
                    <div class="btn-group">
                        <li class="dropdown btn btn-default" style="margin-bottom: 30px">
                            <a href="#" class="dropdown-toggle" style="color: black" data-toggle="dropdown">
                                筛选
                                <b class="caret"></b>
                            </a>
                            <ul class="dropdown-menu">
                                <li><a href="/sell/seller/order/list?filter=-1">全部</a></li>
                                <li><a href="/sell/seller/order/list?filter=0">新订单</a></li>
                                <li><a href="/sell/seller/order/list?filter=1">已完结</a></li>
                                <li><a href="/sell/seller/order/list?filter=2">已取消</a></li>
                                <li><a href="/sell/seller/order/list?filter=3">申请退款</a></li>
                            </ul>
                        </li>
                            <#if status=="true">
                            <a href="/sell/seller/order/setPay?status=false" type="button"
                               class="btn btn-default btn-danger" style="margin-left: 30px">打烊</a>
                            <#elseif status=="false">
                            <a href="/sell/seller/order/setPay?status=true" type="button"
                               class="btn btn-default btn-success" style="margin-left: 30px">营业</a>
                            </#if>
                    </div>
                </div>
            </div>
            <div class="row clearfix">
                <div class="col-md-12 column">

                    <table class="table table-bordered table-condensed">
                        <thead>
                        <tr>
                            <th>订单ID</th>
                            <th>姓名</th>
                            <th>手机号</th>
                            <th>地址</th>
                            <th>金额</th>
                            <th>订单状态</th>
                            <th>支付状态</th>
                            <th>创建时间</th>
                            <th colspan="2">操作</th>
                        </tr>
                        </thead>
                        <tbody>
                    <#list orderDTOPage.content as orderDTO>
                    <tr>
                        <td>${orderDTO.orderId}</td>
                        <td>${orderDTO.buyerName}</td>
                        <td>${orderDTO.buyerPhone}</td>
                        <td>${orderDTO.buyerAddress}</td>
                        <td>${orderDTO.orderAmount}</td>
                        <td>${orderDTO.getOrderStatusEnum().message}</td>
                        <td>${orderDTO.getPayStatusEnum().getMessage()}</td>
                        <td>${orderDTO.createTime}</td>
                        <td><a href="/sell/seller/order/detail?orderId=${orderDTO.orderId}">详情</a></td>
                        <td>
                             <#if orderDTO.getOrderStatusEnum().message=="新订单">
                                 <a href="#" onclick="showTip('${orderDTO.orderId}')">取消</a>
                             <#--<a href="/sell/seller/order/cancel?orderId=${orderDTO.orderId}">取消</a>-->
                             </#if>
                        </td>
                    </tr>
                    </#list>
                    <#if (difference gt 0)>
                        <#list 1..difference as t>
                          <tr>
                              <td>-</td>
                              <td>-</td>
                              <td>-</td>
                              <td>-</td>
                              <td>-</td>
                              <td>-</td>
                              <td>-</td>
                              <td>-</td>
                              <td>-</td>
                              <td>-</td>
                          </tr>
                        </#list>
                    </#if>
                        </tbody>
                    </table>
                </div>

            </div>
        <#--分页-->
            <div class="col-md-12 column">
                <ul class="pagination pull-right">
                    <#if currentPage lte 1>
                    <li class="disabled"><a href="#">上一页</a></li>
                    <#else>
                    <li><a href="/sell/seller/order/list?page=${currentPage - 1}&size=${size}&filter=${filter}">上一页</a>
                    </li>
                    </#if>
                    <#if total lte 10>
                        <#list 1..total as index>
                            <#if currentPage==index>
                    <li class="disabled"><a href="#">${index}</a></li>
                            <#else>
                    <li><a href="/sell/seller/order/list?page=${index}&size=${size}&filter=${filter}">${index}</a></li>
                            </#if>
                        </#list>

                    <#elseif total gte 11>
                    <#--当前页小于4-->
                        <#if currentPage lt 5>
                            <#list 1..5 as index>
                                <#if currentPage==index>
                             <li class="disabled"><a href="#">${index}</a></li>
                                <#else>
                             <li>
                                 <a href="/sell/seller/order/list?page=${index}&size=${size}&filter=${filter}">${index}</a>
                             </li>
                                </#if>
                            </#list>
                    <li class="disabled"><a href="#">...</a></li>
                    <li><a href="/sell/seller/order/list?page=${total}&size=${size}&filter=${filter}">${total}</a></li>
                        </#if>
                    <#--当前页大于4小于等于total-4-->
                        <#if (currentPage gte 5)&&(currentPage lt total-3)>
                    <li><a href="/sell/seller/order/list?page=1&size=${size}&filter=${filter}">1</a></li>
                    <li class="disabled"><a href="#">...</a></li>
                            <#list currentPage-2..currentPage+2 as index>
                                <#if currentPage==index>
                             <li class="disabled"><a href="#">${index}</a></li>
                                <#else>
                              <li>
                                  <a href="/sell/seller/order/list?page=${index}&size=${size}&filter=${filter}">${index}</a>
                              </li>
                                </#if>
                            </#list>
                    <li class="disabled"><a href="#">...</a></li>
                    <li><a href="/sell/seller/order/list?page=${total}&size=${size}&filter=${filter}">${total}</a></li>
                        </#if>
                    <#--当前页大于total-4-->
                        <#if currentPage gte total-3>
                    <li><a href="/sell/seller/order/list?page=1&size=${size}&filter=${filter}">1</a></li>
                    <li class="disabled"><a href="#">...</a></li>
                            <#list total-4..total as index>
                                <#if currentPage==index>
                             <li class="disabled"><a href="#">${index}</a></li>
                                <#else>
                             <li>
                                 <a href="/sell/seller/order/list?page=${index}&size=${size}&filter=${filter}">${index}</a>
                             </li>
                                </#if>
                            </#list>

                        </#if>
                    </#if>
                    <#if currentPage gte orderDTOPage.getTotalPages()>
                    <li class="disabled"><a href="#">下一页</a></li>
                    <#else>
                    <li><a href="/sell/seller/order/list?page=${currentPage + 1}&size=${size}&filter=${filter}">下一页</a>
                    </li>
                    </#if>
                </ul>
            </div>
        </div>
    </div>
</div>
</div>

<#--弹窗-->
<div class="modal fade" id="myModal" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
                <h4 class="modal-title" id="myModalLabel">
                    提醒
                </h4>
            </div>
            <div class="modal-body">
                你有新的订单
            </div>
            <div class="modal-footer">
                <button onclick="javascript:document.getElementById('notice').pause()" type="button"
                        class="btn btn-default" data-dismiss="modal">关闭
                </button>
                <button onclick="location.reload()" type="button" class="btn btn-primary">查看新的订单</button>
            </div>
        </div>
    </div>
</div>
<#--取消提示-->


<#--播放音乐-->
<audio id="notice" loop="loop">
    <source src="/sell/mp3/song.mp3" type="audio/mpeg"/>
</audio>
<script>
    var websocket = null;
    if ('WebSocket' in window) {
        websocket = new WebSocket('wss://www.yzykkpl.cn/sell/webSocket');
    } else {
        alert('该浏览器不支持websocket!');
    }

    websocket.onopen = function (event) {
        console.log('建立连接');
    }

    websocket.onclose = function (event) {
        console.log('连接关闭');
    }

    websocket.onmessage = function (event) {
        console.log('收到消息:' + event.data)
        //弹窗提醒, 播放音乐
        $('#myModal').modal('show');

        document.getElementById('notice').play();
    }

    websocket.onerror = function () {
        alert('websocket通信发生错误！');
    }

    window.onbeforeunload = function () {
        websocket.close();
    }

    function showTip(orderId) {

        $.prompt("取消订单可能造成退款，您确认取消吗？", {
            title: "警告",
            buttons: { "是的，取消": true, "不取消": false },
            submit: function(e,v,m,f){
                if(v) location.href='/sell/seller/order/cancel?orderId='+orderId;
            }
        });
        // var r=confirm("Press a button!");
        // if (r==true)
        // {
        //     location.href='/sell/seller/order/cancel?orderId='+orderId;
        // }
        // else
        // {
        //     alert("You pressed Cancel!");
        // }
    }


</script>

</body>
</html>
