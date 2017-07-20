var addressData = require('../../../data/posts_data.js');

// address-manager.js
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
      addresses: addressData.addressDatas
    })
  },

  /**
   * 添加地址
   */
  addAddress:function(event){
    wx.chooseAddress({
      success: function (res) {
        console.log(res)
      }
    })
  }
})