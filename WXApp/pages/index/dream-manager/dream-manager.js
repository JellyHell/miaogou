var dreamData = require('../../../data/posts_data.js');

// dream-manager.js
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
      userDreams: dreamData.userDreams
    })
  },

  /**
   * 添加梦想
   */
  addDream:function(event){
    wx.navigateTo({
      url: 'add-dream/add-dream',
    })
  }
})