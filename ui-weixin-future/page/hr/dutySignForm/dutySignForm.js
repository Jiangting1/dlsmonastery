var $util = require("../../../util/util.js");
var app = getApp();
Page({
  data: {
    formData: {},
    formProperty: { images: [] },
    response: {},
    submitDisabled: false,
    submitHidden: false,
    options: null,
  },
  onLoad: function (options) {
    var that = this;
    that.data.options = options;
    wx.showToast({
      title: '加载中',
      icon: 'loading',
      duration: 10000,
      success: function () {
        app.autoLogin(function () {
          that.initPage()
        });
      }
    })
  },
  initPage: function () {
    var that = this;
    that.getLocation();
    if (that.data.options.action == 'add') {
      that.setData({ "formData.dutyDateTime": $util.formatLocalDateTime(new Date()) });
    } else {
      wx.request({
        url: $util.getUrl("basic/hr/dutySign/getForm"),
        data: { id: that.data.options.id },
        header: {
          Cookie: "JSESSIONID=" + app.globalData.sessionId
        },
        success: function (res) {
          that.setData({ formData: res.data });
          that.setData({ "submitHidden": true });
          var images = new Array();
          that.setData({ formData: res.data })
          $util.downloadFile(images, res.data.attachment, app.globalData.sessionId, 1, function () {
            that.setData({ "formProperty.images": images });
          });
        }
      })
    }
  },
  getLocation: function (e) {
    var that = this;
    wx.getLocation({
      type: 'wgs84',
      success: function (res) {
        console.log(res)
        that.setData({ "formData.longitude": res.longitude, "formData.latitude": res.latitude, "formData.accuracy": res.accuracy })
        wx.getNetworkType({
          success: function (res) {
            that.setData({ "formData.netType": res.networkType })
          }
        })
        wx.getSystemInfo({
          success: function (res) {
            that.setData({ "formData.model": res.model })
          }
        })
        wx.request({
          url: $util.getUrl("basic/sys/map/getPoiList?longitude=" + res.longitude + "&latitude=" + res.latitude),
          method: 'GET',
          header: {
            Cookie: "JSESSIONID=" + app.globalData.sessionId
          },
          success: function (res) {
            that.setData({ "formProperty.addressList": res.data });
            wx.hideToast();
          }
        });
      }
    })
  },
  bindAddressChange: function (e) {
    this.setData({
      "formProperty.addressIndex": e.detail.value
    });
  },
  addImage: function (e) {
    var that = this;
    var images = that.data.formProperty.images;
    wx.chooseImage({
      count: 1,
      sizeType: ['compressed'],
      sourceType: ['camera'],
      success: function (res) {
        var tempFilePaths = res.tempFilePaths
        wx.uploadFile({
          url: $util.getUrl('general/sys/folderFile/upload'),
          header: {
            Cookie: "JSESSIONID=" + app.globalData.sessionId
          },
          filePath: tempFilePaths[0],
          name: 'file',
          formData: {
            uploadPath: 'dutySign'
          },
          success: function (res) {
            var folderFile = JSON.parse(res.data)[0];
            $util.downloadFile(images, folderFile.id, app.globalData.sessionId, 1, function () {
              that.setData({ "formProperty.images": images });
            });
          }
        })
      }
    })
  },
  showImageActionSheet: function (e) {
    var that = this;
    var index = e.target.dataset.index;
    var itemList = ['预览', '删除'];
    wx.showActionSheet({
      itemList: itemList,
      success: function (res) {
        if (!res.cancel) {
          if (itemList[res.tapIndex] == '预览') {
            wx.previewImage({
              current: that.data.formProperty.images[index].view, // 当前显示图片的http链接
              urls: [that.data.formProperty.images[index].view],
            })
          } else {
            that.data.formProperty.images.splice(index, 1);
            that.setData({ "formProperty.images": that.data.formProperty.images });
          }
        }
      }
    });
  },
  formSubmit: function (e) {
    var that = this;
    that.setData({ submitDisabled: true });
    e.detail.value.attachment = $util.getImageStr(that.data.formProperty.images, app.globalData.sessionId);
    wx.request({
      url: $util.getUrl("basic/hr/dutySign/save"),
      data: e.detail.value,
      header: {
        Cookie: "JSESSIONID=" + app.globalData.sessionId
      },
      success: function (res) {
        console.log(res)
        if (res.data.success) {
          wx.navigateBack();
        } else {
          that.setData({ "response.error": res.data.message, "response.data": res.data.extra.errors, submitDisabled: false });
        }
      }
    })
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