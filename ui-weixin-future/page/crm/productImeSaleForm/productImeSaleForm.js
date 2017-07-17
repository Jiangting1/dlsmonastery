//获取应用实例
var app = getApp();
var $util = require("../../../util/util.js");
Page({
  data: {
    productImeSearchResult: [],
    formData: { imeStr: '' },
    formProperty: {},
    response: {},
    shops: [],
    submitDisabled: false,
    saleShopId: null,
  },
  onLoad: function (option) {
    var that = this;
    app.autoLogin(function () {
      that.initPage()
    })
  },
  initPage: function () {
    var that = this;
    wx.request({
      url: $util.getUrl("ws/future/crm/productImeSale/getSaleForm"),
      method: 'GET',
      header: {
        Cookie: "JSESSIONID=" + app.globalData.sessionId
      },
      success: function (res) {
        that.setData({ formProperty: res.data })
      }
    })
  },
  imeScan: function () {
    var that = this;
    wx.scanCode({
      success: function (res) {
        that.setData({ "formData.imeStr": res.result });
        that.imeSearch();
      }
    })
  },
  imeChange: function (e) {
    var that = this;
    that.setData({ "formData.imeStr": e.detail.value })
    that.imeSearch();
  },
  imeSearch: function () {
    var that = this;
    if ($util.isBlank(that.data.formData.imeStr)) {
      that.setData({ productImeSearchResult: {} })
    } else {
      wx.request({
        url: $util.getUrl("ws/future/crm/productImeSale/checkForSale"),
        data: that.data.formData,
        header: {
          Cookie: "JSESSIONID=" + app.globalData.sessionId
        },
        success: function (res) {
          if (res.errMsg) {
            that.setData({ "response.error": res.data });
          }
        }
      });
      wx.request({
        url: $util.getUrl("ws/future/crm/productImeSale/findProductImeForSaleDto"),
        data: that.data.formData,
        header: {
          Cookie: "JSESSIONID=" + app.globalData.sessionId
        },
        success: function (res) {
          console.log(res.data)
          that.setData({ shops: res.data[0].accessChainDepotList })
          that.setData({ productImeSearchResult: res.data });
        }
      })
    }
  },
  bindSaleShop: function (e) {
    var that = this;
    that.setData({
      'formData.id': that.data.shops[e.detail.value].id,
      'formData.name': that.data.shops[e.detail.value].name,
      saleShopId: that.data.shops[e.detail.value].id
    })
  },
  formSubmit: function (e) {
    var that = this;
    that.setData({ submitDisabled: true });
    if (that.data.productImeSearchResult.length!=0){
      console.log(that.data.productImeSearchResult)

      var productImeSaleDetailList = new Array();
      if (that.data.saleShopId) {
        productImeSaleDetailList.push({ saleShopId: that.data.saleShopId, productImeId: that.data.productImeSearchResult[0].id });
      } else {
        productImeSaleDetailList.push({ saleShopId: null, productImeId: that.data.productImeSearchResult[0].id });
      }
      e.detail.value.productImeSaleDetailStr = JSON.stringify(productImeSaleDetailList);
      wx.request({
        url: $util.getUrl("ws/future/crm/productImeSale/sale"),
        data: e.detail.value,
        header: {
          Cookie: "JSESSIONID=" + app.globalData.sessionId,
        },
        success: function (res) {
          if (res.data.success) {
            that.setData({ "response.data": res.data });
            wx.navigateBack();
          } else if (res.data.message) {
            that.setData({ "response.error": res.data.message })
          } else {
            that.setData({ "response.error": res.data });
          }
          that.setData({ submitDisabled: false });
        }
      })
    }else{
      that.setData({ submitDisabled: false });
    }
  },
  showError: function (e) {
    var that = this;
    var key = e.currentTarget.dataset.key;
    var responseData = that.data.response.data;
    if (responseData && responseData[key] != null) {
      that.setData({ "response.error": responseData[key].message });
      delete responseData[key];
      that.setData({ "response.data": responseData })
    } else {
      that.setData({ "response.error": '' })
    }
  }
})