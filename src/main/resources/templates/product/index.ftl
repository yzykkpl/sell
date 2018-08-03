<html>
<head>
    <meta charset="utf-8">
    <title>塬仓-商品列表</title>
    <link href="https://cdn.bootcss.com/bootstrap/3.3.5/css/bootstrap.min.css" rel="stylesheet">
    <link href="/sell/css/style.css" rel="stylesheet">
</head>
<body>
<div id="wrapper" class="toggled">
<#--边栏-->
            <#include "../common/nav.ftl">
<#--内容区-->
    <div id="page-content-wrapper">
        <div class="container-fluid">
            <div class="row clearfix">
                <div class="col-md-12 column">
                    <form role="form" method="post" action="/sell/seller/product/save">
                        <div class="form-group">
                            <label>名称</label>
                            <input name="productName" class="form-control"  type="text" value="${(productInfo.productName)!""}" />
                        </div>
                        <div class="form-group">
                            <label>价格</label>
                            <input name="productPrice" class="form-control"  type="number" value="${(productInfo.productPrice)!""}" />
                        </div>
                        <div class="form-group">
                            <label>库存</label>
                            <input name="productStock" class="form-control"  type="number" value="${(productInfo.productStock)!""}" />
                        </div>
                        <div class="form-group">
                            <label>描述</label>
                            <input name="productDescription" class="form-control"  type="text" value="${(productInfo.productDescription)!""}" />
                        </div>
                        <div class="form-group">
                            <label>图片</label>
                            <img src="${(productInfo.productIcon)!""}" height="200" width="200">
                            <input name="productIcon" class="form-control"  type="text" value="${(productInfo.productIcon)!''}" />
                        </div>
                        <div class="form-group">
                            <label>详情图片</label>
                            <input name="detailImage" class="form-control"  type="text" value="${(productInfo.detailImage)!''}" />
                        </div>
                        <div class="form-group">
                            <label>类目</label>
                            <select name="categoryType" class="form-control">
                                <#list categoryList as category>
                                    <option value="${category.categoryType}"
                                            <#if (productInfo.categoryType)?? && productInfo.categoryType == category.categoryType>
                                            selected
                                            </#if>>
                                        ${category.categoryName}
                                    </option>
                                </#list>
                            </select>

                        </div>
                        <input  hidden type="text" name="productId" value="${(productInfo.productId)!''}">
                       <button type="submit" class="btn btn-default">提交</button>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>

</body>
</html>
