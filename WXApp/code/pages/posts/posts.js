var utils = require('../../utils/util.js');
var httpservice = require('../../http/httpservice.js');
// posts.js
Page({

  /**
     * 页面的初始数据
     */
  data: {
    products: []
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    var that = this
    httpservice.getMovieListData("https://api.douban.com/v2/movie/top250", function (data) {
      that.processDoubanData(data)
    })
  },

  /**
   * 处理豆瓣数据
   */
  processDoubanData: function (doubanMovies) {
    var products = this.data.products

    //封装电影列表数据
    for (var idx in doubanMovies.subjects) {
      var subject = doubanMovies.subjects[idx]
      var title = subject.title
      if (title.length > 10) {
        title = title.substring(0, 10) + "..."
      }
      var temp = {
        title: title,
        coverageUrl: subject.images.large,
        movieId: subject.id
      }
      products.push(temp)
    }

    this.setData({
      products: products
    })
  },

  /**
   * Banner列表点击
   */
  onSwiperTap:function(event){
    
  },

  /**
   * 点击去签到 
   */
  onToSignTap:function(event){
    wx.switchTab({
      url: '../index/index',
    })
  },

  /**
   * 点击去销量榜
   */
  onToPopular: function (event) {
    wx.navigateTo({
      url: "../category/category_detail/category_detail?categoryTitle=" + "销量榜"
    })
  },


  /**
   * 点击去心愿单
   */
  onToDream: function (event) {
    wx.navigateTo({
      url: "../index/dream-manager/dream-manager"
    })
  },

  /**
   * 点击去个人中心
   */
  onToDetail: function (event) {
    wx.navigateTo({
      url: "post-detail/post-detail"
    })
  }
})