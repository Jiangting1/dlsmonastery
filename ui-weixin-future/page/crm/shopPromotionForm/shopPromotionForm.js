var app = getApp();
var $util = require("../../../util/util.js");
Page({
  data: {
    formData: {},
    formProperty: {},
    response: {},
    submitDisabled: false,
    submitHidden: false,
  },
  onLoad: function (options) {
    var that = this;
    that.data.options = options
    app.autoLogin(function () {
      that.initPage()
    })
  },
  initPage: function () {
    var that = this;
    var options = that.data.options;
    wx.request({
      url: $util.getUrl("ws/future/layout/shopPromotion/getForm"),
      data: {},
      method: 'GET',
      header: { Cookie: "JSESSIONID=" + app.globalData.sessionId },
      success: function (res) {
        that.setData({ 'formProperty.activityTypeList': res.data.extra.activityTypeList })
      }
    })
    if (options.action == "update") {
      that.detail()
    } else if (options.action == "detail") {
      that.detail();
      that.setData({ submitHidden: !that.data.submitHidden })
    }
  },
  bindShop: function (e) {
    wx.navigateTo({
      url: '/page/crm/depotSearch/depotSearch?category=SHOP'
    })
  },
  bindActivityDate: function (e) {
    var that = this;
    that.setData({ "formData.activityDate": e.detail.value })
  },
  bindActivityType: function (e) {
    var that = this;
    that.setData({ 'formData.activityType': that.data.formProperty.activityTypeList[e.detail.value] })
  },
  addImage: function (e) {
    var that = this;
    let images = [];
    let name = e.target.dataset.name;
    wx.chooseImage({
      count: 9,
      sizeType: ['compressed', 'original'],
      sourceType: ['album', 'camera'],
      success: function (res) {
        var tempFilePaths = res.tempFilePaths;
        for (let i in tempFilePaths) {
          wx.uploadFile({
            url: $util.getUrl('general/sys/folderFile/upload'),
            header: {
              Cookie: "JSESSIONID=" + app.globalData.sessionId
            },
            filePath: tempFilePaths[i],
            name: 'file',
            formData: {
              uploadPath: 'shopPromotion'
            },
            success: function (res) {
              var folderFile = JSON.parse(res.data)[0];
              $util.downloadFile(images, folderFile.id, app.globalData.sessionId, 9, function () {
                that.data.formProperty[name] = images;
                that.setData({ formProperty: that.data.formProperty });
              });
            }
          })
        }
      }
    })
  },
  showImageActionSheet: function (e) {
    var that = this;
    var index = e.target.dataset.index;
    var activityImage = e.target.dataset.name;
    var itemList = ['预览', '删除'];
    wx.showActionSheet({
      itemList: itemList,
      success: function (res) {
        if (!res.cancel) {
          if (itemList[res.tapIndex] == '预览') {
            var images = that.data.formProperty[activityImage];
            wx.previewImage({
              current: images[index].view, // 当前显示图片的http链接
              urls: [images[index].view]
            })
          } else {
            that.data.formProperty[activityImage].splice(index, 1);
            that.setData({ 'formProperty': that.data.formProperty })
          }
        }
      }
    });
  },
  formSubmit: function (e) {
    var that = this;
    that.setData({ submitDisabled: true });
    e.detail.value.activityImage1 = $util.getImageStr(that.data.formProperty.activityImage1, app.globalData.sessionId);
    e.detail.value.activityImage2 = $util.getImageStr(that.data.formProperty.activityImage2, app.globalData.sessionId);
    e.detail.value.activityImage3 = $util.getImageStr(that.data.formProperty.activityImage3, app.globalData.sessionId);
    wx.request({
      url: $util.getUrl("ws/future/layout/shopPromotion/save"),
      data: e.detail.value,
      header: { Cookie: "JSESSIONID=" + app.globalData.sessionId },
      success: function (res) {
        console.log(res.data)
        if (res.data.success) {
          wx.navigateBack();
        } else if (res.data.extra.hasOwnProperty("errots")) {
          that.setData({ 'response.data': res.data.extra.errors, submitDisabled: false });
        } else {
          that.setData({ "response.error": res.data.message, submitDisabled: false })
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
    } else if (res.data.hasOwnProperty("extra")) {
      that.setData({ 'response.data': res.data.extra.errors, submitDisabled: false });
    } else {
      that.setData({ "response.error": res.data, submitDisabled: false })
    }
  },
  detail: function () {
    var that = this;
    wx.request({
      url: $util.getUrl("ws/future/layout/shopPromotion/findOne"),
      data: { id: that.data.options.id },
      method: 'GET',
      header: { Cookie: "JSESSIONID=" + app.globalData.sessionId },
      success: function (res) {
        let images1 = new Array();
        let images2 = new Array();
        let images3 = new Array();

        that.setData({ formData: res.data })
        $util.downloadFile(images1, res.data.activityImage1, app.globalData.sessionId, 9, function () {
          that.setData({ "formProperty.activityImage1": images1 });
        });
        $util.downloadFile(images2, res.data.activityImage2, app.globalData.sessionId, 9, function () {
          that.setData({ "formProperty.activityImage2": images2 });
        });
        $util.downloadFile(images3, res.data.activityImage3, app.globalData.sessionId, 9, function () {
          that.setData({ "formProperty.activityImage3": images3 });
        });
      }
    })
  }
})