var integralData = require('../../../data/posts_data.js');

// integral-manager.js
Page({
  /**
   * 页面的初始数据
   */
  data: {
  
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    this.setData({
      integrals: integralData.userIntegrals
    })
  },

  seeDesc:function(event){
    wx.navigateTo({
      url: 'integral-intro/integral-intro',
    })
  }
})