//获取应用实例
var app = getApp();
var $util = require("../../../util/util.js");
Page({
  data: {
    page: {},
    formData: {
      page: 0,
      size: 10
    },
    activeItem: null,
    searchHidden: true
  },
  onLoad: function (option) {
  },
  onShow: function () {
    var that = this;
    app.autoLogin(function () {
      that.initPage()
    });
  },
  initPage: function () {
    var that = this;
    that.pageRequest();
  },
  pageRequest: function () {
    var that = this
    wx.showToast({
      title: '加载中',
      icon: 'loading',
      duration: 10000,
      success: function (res) {
        wx.request({
          url: $util.getUrl("basic/hr/dutyOvertime"),
          header: {
            'x-auth-token': app.globalData.sessionId,
            'authorization': "Bearer" + wx.getStorageSync('token').access_token
          },
          data: that.data.formData,
          success: function (res) {
            that.setData({ page: res.data });
            wx.hideToast();
          }
        })
      }
    })
  },
  add: function () {
    wx.navigateTo({
      url: '/page/hr/dutyOvertimeForm/dutyOvertimeForm'
    })
  },
  search: function () {
    var that = this;
    that.setData({
      searchHidden: !that.data.searchHidden
    })
  },
  bindDateChange: function (e) {
    var that = this;
    var name = e.currentTarget.dataset.name;
    if (name == 'dutyDateStart') {
      that.setData({ "formData.dutyDateStart": e.detail.value });
    } else {
      that.setData({ "formData.dutyDateEnd": e.detail.value });
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
    wx.showActionSheet({
      itemList: ["删除"],
      success: function (res) {
        if (!res.cancel) {
          if (res.tapIndex == 0) {
            wx.request({
              url: $util.getUrl("basic/hr/dutyOvertime/delete"),
              data: { id: id },
              header: {
                'x-auth-token': app.globalData.sessionId,
                'authorization': "Bearer" + wx.getStorageSync('token').access_token
              },
              success: function (res) {
                that.pageRequest();
              }
            })
          }
        }
      }
    })
  },
  formSubmit: function (e) {
    var that = this;
    that.setData({ searchHidden: !that.data.searchHidden, formData: e.detail.value, "formData.pageNumber": 0 });
    that.pageRequest();
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