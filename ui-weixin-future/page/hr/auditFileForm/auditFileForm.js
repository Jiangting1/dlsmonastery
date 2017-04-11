// page/hr/dutyFreeForm/dutyFreeForm.js
var app = getApp();
var $util = require("../../../util/util.js");
Page({
  data: {
    formData: { },
    formProperty:{},
    response: {},
    submitDisabled: false
  },
  onLoad: function (options) {
    var that = this;
    that.data.options = options
    app.autoLogin(function(){
      that.initPage()
    })
  },
  initPage: function () {
    var that = this;
    var options = that.data.options;
    wx.request({
      url: $util.getUrl("hr/auditFile/getFormProperty"),
      data: {},
      method: 'GET',
      header: { 'x-auth-token': app.globalData.sessionId },
      success: function (res) {
        that.setData({ 'formProperty.processList': res.data.processTypes })
      }
    })
    if ( options.action == "update") {
      wx.request({
        url: $util.getUrl("hr/auditFile/detail"),
        data: { id:  options.id },
        method: 'GET',
        header: { 'x-auth-token': app.globalData.sessionId },
        success: function (res) {
          that.setData({ formData: res.data })
        }
      })
    }
  },
  bindFreeDate: function (e) {
    var that = this;
    that.setData({
      'formData.freeDate': e.detail.value
    });
  },
  bindDateType: function (e) {
    var that = this;
    that.setData({
      'formData.dateType': that.data.dateList[e.detail.value]
    });
  },
  bindProcessType: function (e) {
    var that = this;
    that.setData({
      'formData.processType.id': that.data.formProperty.processList[e.detail.value].id,
      'formData.processType.name': that.data.formProperty.processList[e.detail.value].name
    })
  },
  formSubmit: function (e) {
    var that = this;
    that.setData({ submitDisabled: true });
    wx.request({
      url: $util.getUrl("hr/auditFile/save"),
      data: e.detail.value,
      header: {'x-auth-token': app.globalData.sessionId},
      success: function (res) {
        if (res.data.success) {
          wx.navigateBack();
        } else {
          that.setData({ 'response.data': res.data, submitDisabled: false });
        }
      }
    })
  },
  showError: function (e) {
    var that = this;
    var key = e.currentTarget.dataset.key;
    var responseData = that.data.response.data;
    if(responseData && responseData.errors && responseData.errors[key] != null) {
      that.setData({ "response.error": responseData.errors[key].message });
      delete responseData.errors[key];
      that.setData({ "response.data": responseData })
    } else {
      that.setData({ "response.error": '' })
    }
  },
})