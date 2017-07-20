var utils = require('../../../utils/util.js');
var httpservice = require('../../../http/httpservice.js');
var orderTypesData = require('../../../data/posts_data.js');

// order-manager.js
Page({
  /**
   * 页面的初始数据
   */
  data: {
    orderTypes:[],
    orders: [],
    currentIndex:0
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    this.setData({
      orderTypes: orderTypesData.orderTypes,
    })

    this.processOrderData()
  },

  /**
   * 切换顶部Tab
   */
  onClickType:function(event){
    var orderTypes = this.data.orderTypes
    var selectedIndex = event.currentTarget.dataset.index
    var currentIndex = this.data.currentIndex
    orderTypes[currentIndex].orderTypeSelected = false
    orderTypes[selectedIndex].orderTypeSelected = true
    this.setData({
      orderTypes: orderTypes,
      currentIndex: selectedIndex
    })

    this.processOrderData()

  },

  /**
   * 处理豆瓣数据
   */
  processOrderData: function () {
    var allOrders = orderTypesData.orderDatas
    var selectedOrder = []

    //封装电影列表数据
    for (var idx in allOrders) {
      var order = allOrders[idx]
      if (order.orderType == this.data.currentIndex){
        var price = 0
        for (var idy in order.orderProducts){
          price = price + order.orderProducts[idy].productPrice
        }
        order.totalPrice = price
        selectedOrder.push(order)
      }
      
    }
    this.setData({
      orders: selectedOrder
    })
  }

})