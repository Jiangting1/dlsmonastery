//获取应用实例
var app = getApp();
var $util = require("../../../util/util.js");
Page({
  data: {
    page: {},
    formData: {},
    formProperty: {},
    searchHidden: true,
    activeItem: null,
    scrollTop: null,
    height: null
  },
  onLoad: function (option) {
    this.setData({ height: $util.getWindowHeight() })
  },
  onShow: function () {
    var that = this;
    wx.showToast({
      title: '加载中',
      icon: 'loading',
      duration: 10000,
      success: function (res) {
        app.autoLogin(function () {
          that.initPage()
        });
      }
    })
  },
  initPage: function () {
    var that = this;
    wx.request({
      url: $util.getUrl("ws/future/layout/shopAd/getQuery"),
      data: {},
      method: 'GET',
      header: {
        Cookie: "JSESSIONID=" + app.globalData.sessionId
      },
      success: function (res) {
        that.setData({ formData: res.data });
        that.setData({ 'formProperty.shopAdTypeList': res.data.extra.shopAdTypes });
        wx.request({
          url: $util.getUrl("general/sys/processFlow/findByProcessTypeName?processTypeName=广告申请"),
          data: {},
          method: 'GET',
          header: {
            Cookie: "JSESSIONID=" + app.globalData.sessionId
          },
          success: function (res) {
            that.setData({ 'formProperty.processList': res.data })
          }
        })
        that.pageRequest();
      }
    });
  },
  pageRequest: function () {
    var that = this;
    wx.request({
      url: $util.getUrl("ws/future/layout/shopAd"),
      header: {
        Cookie: "JSESSIONID=" + app.globalData.sessionId
      },
      data: $util.deleteExtra(that.data.formData),
      success: function (res) {
        var content = res.data.content;
        for (var item in content) {
          var actionList = new Array();
          actionList.push("详细");
          if (content[item].isAuditable && content[item].processStatus !== "已通过" && content[item].processStatus !== "未通过") {
            actionList.push("审核");
          }
          if (content[item].isEditable && !content[item].locked) {
            actionList.push("修改", "删除");
          }
          res.data.content[item].actionList = actionList;
        }
        that.setData({ page: res.data });
        wx.hideToast();
        that.setData({ scrollTop: $util.toUpper() });
      }
    })
  },
  add: function () {
    wx.navigateTo({
      url: '/page/crm/shopAdForm/shopAdForm'
    })
  },
  search: function () {
    var that = this;
    that.setData({ searchHidden: !that.data.searchHidden })
  },
  formSubmit: function (e) {
    var that = this;
    that.setData({ searchHidden: !that.data.searchHidden, formData: e.detail.value, "formData.page": 0 });
    wx.showToast({
      title: '加载中',
      icon: 'loading',
      duration: 10000,
      success: function (res) {
        that.pageRequest();
      }
    })
  },
  bindAdType: function (e) {
    var that = this;
    that.setData({
      'formData.shopAdTypeId': that.data.formProperty.shopAdTypeList[e.detail.value].id,
      'formData.shopAdTypeName': that.data.formProperty.shopAdTypeList[e.detail.value].name
    })
  },
  bindStatus: function (e) {
    var that = this;
    that.setData({ 'formData.processStatus': that.data.formProperty.processList[e.detail.value].name })
  },
  bindDateChange: function (e) {
    var that = this;
    var name = e.currentTarget.dataset.name;
    if (name == 'createdDateStart') {
      that.setData({ "formData.createdDateStart": e.detail.value });
    } else {
      that.setData({ "formData.createdDateEnd": e.detail.value });
    }
  },
  itemActive: function (e) {
    var that = this;
    var id = e.currentTarget.dataset.id;
    for (var index in that.data.page.content) {
      var item = that.data.page.content[index];
      if (item.id == id) {
        that.data.activeItem = item;
        item.active = true;
      } else {
        item.active = false;
      }
    }
    that.setData({ page: that.data.page });
  },
  showActionSheet: function (e) {
    var that = this;
    var id = e.currentTarget.dataset.id;
    var itemList = that.data.activeItem.actionList;
    if (itemList.length == 0) {
      return;
    }
    wx.showActionSheet({
      itemList: itemList,
      success: function (res) {
        if (!res.cancel) {
          if (itemList[res.tapIndex] == "修改") {
            wx.navigateTo({
              url: '/page/crm/shopAdForm/shopAdForm?action=update&id=' + id
            })
          } else if (itemList[res.tapIndex] == "详细") {
            wx.navigateTo({
              url: '/page/crm/shopAdForm/shopAdForm?action=detail&id=' + id
            })
          }
          else if (itemList[res.tapIndex] == "删除") {
            wx.request({
              url: $util.getUrl("ws/future/layout/shopAd/delete"),
              data: { id: id },
              header: {
                Cookie: "JSESSIONID=" + app.globalData.sessionId
              },
              success: function (res) {
                that.pageRequest();
              }
            })
          } else if (itemList[res.tapIndex] == "审核") {
            wx.navigateTo({
              url: '/page/crm/shopAdForm/shopAdForm?action=audit&id=' + id
            })
          }
        }
      }
    })
  },
  toFirstPage: function () {
    var that = this;
    that.setData({ "formData.page": 0 });
    that.pageRequest();
  },
  toPreviousPage: function () {
    var that = this;
    that.setData({ "formData.page": $util.getPreviousPageNumber(that.data.formData.page) });
    that.pageRequest();
  },
  toNextPage: function () {
    var that = this;
    that.setData({ "formData.page": $util.getNextPageNumber(that.data.formData.page, that.data.page.totalPages) });
    that.pageRequest();
  },
  toLastPage: function () {
    var that = this;
    that.setData({ "formData.page": that.data.page.totalPages - 1 });
    that.pageRequest();
  },
  toPage: function () {
    var that = this;
    var itemList = $util.getPageList(that.data.formData.page, that.data.page.totalPages);
    wx.showActionSheet({
      itemList: itemList,
      success: function (res) {
        if (!res.cancel) {
          that.setData({ "formData.page": itemList[res.tapIndex] - 1 });
          that.pageRequest();
        }
      }
    });
  }
})