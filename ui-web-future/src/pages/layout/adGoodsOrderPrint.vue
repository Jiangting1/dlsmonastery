<template>
  <div style="width:794px;height:562px;margin:auto;">
    <br/>
    <h2 style="text-align: center;">{{adGoodsOrder.expressCompanyName}}物料库出库单({{adGoodsOrder.id}})</h2>
    <div class="row">
      <div class="span3">
      财务单号{{adGoodsOrder.outCode}}
      </div>
      <div class="span3">
        开单日期：{{adGoodsOrder.billDate}}
      </div>
      <div class="span3">
        出货单号：{{adGoodsOrder.formatId}}
      </div>
    </div>
    <div class="row">
      <div class="span10">
        收货单位：{{adGoodsOrder.shopName}}
      </div>
      <div class="span10">
        摘要：{{adGoodsOrder.expressOrderExpressCompanyName}}，{{adGoodsOrder.expressOrderAddress}}，{{adGoodsOrder.expressOrderMobilePhone}}
        <b style="font-size: 18px;font-family: '楷体';">&nbsp;{{adGoodsOrder.billRemarks}}</b><br/>
        收货人：&nbsp;{{adGoodsOrder.expressOrderContator}}，{{adGoodsOrder.expressOrderMobilePhone}}，{{adGoodsOrder.billAddress }}， 业务：{{adGoodsOrder.employeeName }}&nbsp;{{adGoodsOrder.employeeMobilePhone}}
        &nbsp;地址 ：{{adGoodsOrder.shopAddress}}
      </div>
    </div>
    <table class="table table-bordered">
      <tbody class="list">
      <tr>
        <td class="span1">编码</td>
        <td class="span4">货品名称</td>
        <td class="span1">数量</td>
        <td class="span1">单价</td>
        <td class="span1">货款</td>
      </tr>
      <tr v-for="item in adGoodsOrderDetailList">
        <td class="span1">{{item.productCode}}</td>
        <td class="span4">{{item.productName}}</td>
        <td class="span1">{{item.billQty}}</td>
        <td class="span1">{{item.productPrice2}}</td>
        <td class="span1">{{item.billQty * item.productPrice2}}</td>
      </tr>
      <tr>
        <td>合计</td>
        <td></td>
        <td>{{totalBillQty}}</td>
        <td></td>
        <td>{{totalPrice.toFixed(2)}}</td>
      </tr>
      </tbody>
    </table>
    <div class="row">
      <div class="span1">审核：</div>
      <div class="span1">记账：</div>
      <div class="span2">收款：</div>
      <div class="span2">仓管员：</div>
      <div class="span2">制单：{{adGoodsOrder.loginName}}</div>
      <div class="span2">提货人：</div>
    </div>
  </div>
</template>
<script>
  export default{
    data(){
      return{
        adGoodsOrder : {},
        adGoodsOrderDetailList:[],
        totalBillQty : 0,
        totalPrice : 0,
      }
    }, methods:{

      findDetailList() {

        return axios.get('/api/ws/future/layout/adGoodsOrder/findDetailListByAdGoodsOrderId',{params: {adGoodsOrderId:this.$route.query.id}});
      },
      print() {
        return axios.get('/api/ws/future/layout/adGoodsOrder/print', {params: {id: this.$route.query.id}});
      }
    },

    created(){

      axios.all([this.findDetailList(), this.print()])
        .then(axios.spread( (findDetailListRes, printRes) => {
          let tempList = new Array();
          for(let index of findDetailListRes.data){
            if(index.billQty >0){
              tempList.push(index);
            }
          }
          this.adGoodsOrderDetailList = tempList;
          this.adGoodsOrder = printRes.data;

          let resultQty = 0;
          let resultPrice = 0;

          if(this.adGoodsOrderDetailList){
            for(let adGoodsOrderDetail of this.adGoodsOrderDetailList){
              resultQty += adGoodsOrderDetail.billQty;
              resultPrice += adGoodsOrderDetail.billQty * adGoodsOrderDetail.productPrice2;
            }
          }
          this.totalBillQty = resultQty;
          this.totalPrice = resultPrice;

          this.$nextTick(()=>{
            window.print();
            this.$router.push({ name: 'adGoodsOrderList'});
          });
        }));
    }
  }
</script>
<style type="text/css">
  body {
    margin: 0;
    font-size: 18px;
    font-weight:500;
    line-height: 26px;
    color: #333;
    background-color: #fff;
  }
  .row {
    margin-left: -20px;
    *zoom: 1;
  }
  .row:before,
  .row:after {
    display: table;
    line-height: 0;
    content: "";
  }
  .row:after {
    clear: both;
  }
  [class*="span"] {
    float: left;
    min-height: 1px;
    margin-left: 20px;
  }
  .span10 {
    width: 780px;
  }
  .span4 {
    width: 300px;
  }
  .span3 {
    width: 220px;
  }
  .span2 {
    width: 140px;
  }
  .span1 {
    width: 60px;
  }
  table {
    max-width: 100%;
    background-color: transparent;
    border-collapse: collapse;
    border-spacing: 0;
  }

  .table {
    width: 100%;
    margin-bottom: 20px;
  }

  .table th,
  .table td {
    padding: 8px;
    line-height: 20px;
    text-align: left;
    vertical-align: top;
    border-top: 1px solid #dddddd;
  }

  .table th {
    font-weight: bold;
  }

  .table thead th {
    vertical-align: bottom;
  }
  .table-bordered {
    border: 1px solid #dddddd;
    border-collapse: separate;
    *border-collapse: collapse;
    border-left: 0;
    -webkit-border-radius: 4px;
    -moz-border-radius: 4px;
    border-radius: 4px;
  }

  .table-bordered th,
  .table-bordered td {
    border-left: 1px solid #dddddd;
  }
  .table td.span1,
  .table th.span1 {
    float: none;
    width: 44px;
    margin-left: 0;
  }

  .table td.span2,
  .table th.span2 {
    float: none;
    width: 124px;
    margin-left: 0;
  }

  .table td.span3,
  .table th.span3 {
    float: none;
    width: 204px;
    margin-left: 0;
  }

  .table td.span4,
  .table th.span4 {
    float: none;
    width: 284px;
    margin-left: 0;
  }

  .table td.span10,
  .table th.span10 {
    float: none;
    width: 764px;
    margin-left: 0;
  }

</style>
