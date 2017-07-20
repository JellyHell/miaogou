//index.js
//获取应用实例
var app = getApp()
Page({
  data: {
    signState: '点击签到',
    userInfo: {}
  },

  goToHomePage: function () {
    wx.navigateTo({
      url: '../posts/posts',
    })
  },

  onLoad: function () {
    console.log('onLoad')
    var that = this
    //调用应用实例的方法获取全局数据
    app.getUserInfo(function (userInfo) {
      //更新数据
      that.setData({
        userInfo: userInfo
      })
    })
  },

  /**
   * 点击签到
   * */
  onSignTap: function (event){
    this.setData({
      signState:'已签到'
    })
  },

  /**
   * 订单管理
   * */
  orderManagerTap: function (event) {
    wx.navigateTo({
      url: "order-manager/order-manager"
    })
  },

  /**
   * 地址管理
   * */
  addressManagerTap: function (event) {
    wx.navigateTo({
      url: "address-manager/address-manager"
    })
  },

  /**
   * 梦想管理
   * */
  dreamManagerTap: function (event) {
    wx.navigateTo({
      url: "dream-manager/dream-manager"
    })
  },

  /**
   * 积分管理
   * */
  integralManagerTap: function (event) {
    wx.navigateTo({
      url: "integral-manager/integral-manager"
    })
  },

  /**
   * 关于我们
   * */
  aboutUsTap: function (event) {
    wx.navigateTo({
      url: "about-us/about-us"
    })
  }
})
