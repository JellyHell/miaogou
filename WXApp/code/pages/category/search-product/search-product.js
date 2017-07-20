var utils = require('../../../utils/util.js');
var httpservice = require('../../../http/httpservice.js');

// search-product.js
Page({

  /**
   * 页面的初始数据
   */
  data: {
    products: [],
    isSearching : false
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    this.setData({
      isSearching : false
    })
  },

  /**
   * 失去焦点
   */
  bindblur: function (event) {
    console.log(event.detail.value)
    var text = event.detail.value;

    this.setData({
      products: []
    })

    if(text.length > 0){
      this.setData({
        isSearching: true
      })

      var searchUrl = "https://api.douban.com/v2/movie/search?q=" + text;
      var that = this
      httpservice.getMovieListData(searchUrl, function (data) {
        that.processDoubanData(data)
      }) 
    }else{
      this.setData({
        isSearching: false
      })
    }
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
      products: products,
      isSearching: false
    })
  }
})