//httpservice.js api接口端

/**
 * 请求豆瓣接口获取数据
 */
function getMovieListData(url,callback) {
  var that = this
  wx.request({
    url: url,
    method: 'GET',
    header: {
      "Content-Type": "application/xml"
    },
    success: function (res) {
      callback(res.data)
    }
  })
}

module.exports = {
  getMovieListData: getMovieListData
}