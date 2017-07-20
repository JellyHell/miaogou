var utils = require('../../../utils/util.js');
var httpservice = require('../../../http/httpservice.js');

// category_detail.js
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
    var categoryTitle = options.categoryTitle;
    wx.setNavigationBarTitle({
      title: categoryTitle,
    })

    var that = this
    httpservice.getMovieListData("https://api.douban.com/v2/movie/in_theaters", function (data) {
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
  }
})