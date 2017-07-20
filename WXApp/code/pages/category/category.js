var postsData = require('../../data/posts_data.js');

// category.js
Page({
  /**
   * 页面的初始数据
   */
  data: {
    categorys:[]
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    this.setData({
      categorys : postsData.postdata
    })
  },

  /**
   * 点击分类
   */
  onTapCategory: function (event){
    console.log(event)

    var categoryTitle = event.currentTarget.dataset.categorytitle
    wx.navigateTo({
      url: "category_detail/category_detail?categoryTitle=" + categoryTitle
    })
  },

  /**
   * 点击搜索
   */
  onSearchTap:function(event){
    wx.navigateTo({
      url: 'search-product/search-product',
    })
  }
})