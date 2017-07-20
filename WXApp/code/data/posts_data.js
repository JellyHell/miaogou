/**
 * 分类数据
 * */ 
var local_database = [
  {
    category_title: "洁面",
    category_img: "/images/categroy/categroy1.jpeg"
  },
  {
    category_title: "水乳",
    category_img: "/images/categroy/categroy2.jpeg"
  },
  {
    category_title: "礼盒",
    category_img: "/images/categroy/categroy3.jpeg"
  },
  {
    category_title: "洗发护发",
    category_img: "/images/categroy/categroy4.jpeg"
  },
  {
    category_title: "沐浴用品",
    category_img: "/images/categroy/categroy5.jpeg"
  },
  {
    category_title: "口腔护理",
    category_img: "/images/categroy/categroy6.jpeg"
  },
  {
    category_title: "女性护理",
    category_img: "/images/categroy/categroy7.jpeg"
  },
  {
    category_title: "身体护理",
    category_img: "/images/categroy/categroy8.jpeg"
  },
  {
    category_title: "手足护理",
    category_img: "/images/categroy/categroy9.jpeg"
  },
  {
    category_title: "底妆",
    category_img: "/images/categroy/categroy10.jpeg"
  },
  {
    category_title: "唇妆",
    category_img: "/images/categroy/categroy11.jpeg"
  },
  {
    category_title: "眼妆",
    category_img: "/images/categroy/categroy12.jpeg"
  },
  {
    category_title: "化妆工具",
    category_img: "/images/categroy/categroy13.jpeg"
  },
  {
    category_title: "香水",
    category_img: "/images/categroy/categroy14.jpeg"
  },
  {
    category_title: "装前",
    category_img: "/images/categroy/categroy15.jpeg"
  },
]


/**
 * 订单分类
 */
var orderType = [
  {
    orderIndex:0,
    orderTypeName: "待付款",
    orderTypeSelected: true
  },
  {
    orderIndex: 1,
    orderTypeName: "待收货",
    orderTypeSelected: false
  },
  {
    orderIndex: 2,
    orderTypeName: "已完成",
    orderTypeSelected: false
  },
  {
    orderIndex: 3,
    orderTypeName: "已取消",
    orderTypeSelected: false
  }

]


/**
 * 订单假数据
 */
var orderDatas = [
  {
    orderType:0,
    managerTitle:"去支付",
    orderProducts:[
      {
        productName:"蒙牛 特仑苏 醇芊调制乳",
        productInfo:"250ml*12 礼盒装",
        productPrice:58,
        productImg:"https://img3.doubanio.com/view/movie_poster_cover/lpst/public/p480747492.jpg"
      }
    ],
    totalPrice:0
  },
  {
    orderType: 0,
    managerTitle: "去支付",
    orderProducts: [
      {
        productName: "蒙牛 特仑苏 醇芊调制乳",
        productInfo: "250ml*12 礼盒装",
        productPrice: 58,
        productImg: "https://img3.doubanio.com/view/movie_poster_cover/lpst/public/p480747492.jpg"
      },
      {
        productName: "蒙牛 特仑苏 醇芊调制乳",
        productInfo: "250ml*12 礼盒装",
        productPrice: 58,
        productImg: "https://img3.doubanio.com/view/movie_poster_cover/lpst/public/p492406163.jpg"
      },
      {
        productName: "蒙牛 特仑苏 醇芊调制乳",
        productInfo: "250ml*12 礼盒装",
        productPrice: 58,
        productImg: "https://img3.doubanio.com/view/movie_poster_cover/lpst/public/p457760035.jpg"
      },
      {
        productName: "蒙牛 特仑苏 醇芊调制乳",
        productInfo: "250ml*12 礼盒装",
        productPrice: 58,
        productImg: "https://img3.doubanio.com/view/movie_poster_cover/lpst/public/p513344864.jpg"
      }
    ],
    totalPrice: 0
  },
  {
    orderType: 1,
    managerTitle: "查看物流",
    orderProducts: [
      {
        productName: "蒙牛 特仑苏 醇芊调制乳",
        productInfo: "250ml*12 礼盒装",
        productPrice: 58,
        productImg: "https://img3.doubanio.com/view/movie_poster_cover/lpst/public/p480747492.jpg"
      }
    ],
    totalPrice: 0
  },
  {
    orderType: 1,
    managerTitle: "查看物流",
    orderProducts: [
      {
        productName: "蒙牛 特仑苏 醇芊调制乳",
        productInfo: "250ml*12 礼盒装",
        productPrice: 58,
        productImg: "https://img3.doubanio.com/view/movie_poster_cover/lpst/public/p480747492.jpg"
      }
    ],
    totalPrice: 0
  },
  {
    orderType: 2,
    managerTitle: "再次购买", 
    orderProducts: [
      {
        productName: "蒙牛 特仑苏 醇芊调制乳",
        productInfo: "250ml*12 礼盒装",
        productPrice: 58,
        productImg: "https://img3.doubanio.com/view/movie_poster_cover/lpst/public/p480747492.jpg"
      }
    ],
    totalPrice: 0
  },
  {
    orderType: 3,
    managerTitle: "再次购买", 
    orderProducts: [
      {
        productName: "蒙牛 特仑苏 醇芊调制乳",
        productInfo: "250ml*12 礼盒装",
        productPrice: 58,
        productImg: "https://img3.doubanio.com/view/movie_poster_cover/lpst/public/p480747492.jpg"
      }
    ],
    totalPrice: 0
  },
  {
    orderType: 2,
    managerTitle: "再次购买", 
    orderProducts: [
      {
        productName: "蒙牛 特仑苏 醇芊调制乳",
        productInfo: "250ml*12 礼盒装",
        productPrice: 58,
        productImg: "https://img3.doubanio.com/view/movie_poster_cover/lpst/public/p480747492.jpg"
      }
    ],
    totalPrice: 0
  }
]

/**
 * 用户地址数据
 */
var userAddres = [
  {
    userName:"程言方",
    userMobile:"18811778682",
    userAddres:"北京市东城区富华大厦F座11层京金所"
  },
  {
    userName: "邱祥浩",
    userMobile: "18811778682",
    userAddres: "上海市首批沿海开放城市"
  }
]

/**
 * 用户心愿单 
 */
var userDream = [
  {
    productDesc:"蒙牛 特仑苏 醇芊调制乳",
    productImg: "https://img3.doubanio.com/view/movie_poster_cover/lpst/public/p480747492.jpg",
    state:0,//未确认
    createTime: "2017.2.3",
    contactTime:"2017.3.4"
  }, 
  {
    productDesc: "蒙牛 特仑苏 醇芊调制乳",
    productImg: "https://img3.doubanio.com/view/movie_poster_cover/lpst/public/p480747492.jpg",
    state: 1,//已确认
    createTime: "2017.2.3",
    contactTime: "2017.3.4"
  }
  , {
    productDesc: "蒙牛 特仑苏 醇芊调制乳",
    productImg: "https://img3.doubanio.com/view/movie_poster_cover/lpst/public/p480747492.jpg",
    state: 2,//已上架
    createTime: "2017.2.3",
    contactTime: "2017.3.4"
  }
]

/**
 * 用户积分假数据 
 */
var userIntegral = [
  {
    type:1,//签到
    count:1,//积分数
    desc:"签到奖励",
    color: "#20A0FF"
  },
  {
    type: 2,//购买商品
    count: 15,//积分数
    desc: "消费奖励",
    color: "#FF4949"
  },
  {
    type: 1,//签到
    count: 1,//积分数
    desc: "签到奖励",
    color: "#20A0FF"
  },
  {
    type: 2,//购买商品
    count: 50,//邀请好友奖励
    desc: "邀请好友奖励",
    color: "#13CE66"
  }, {
    type: 2,//购买商品
    count: 15,//积分数
    desc: "消费奖励",
    color: "#FF4949"
  }

]


module.exports = {
  postdata: local_database,
  orderTypes: orderType,
  orderDatas: orderDatas,
  addressDatas: userAddres,
  userDreams: userDream,
  userIntegrals: userIntegral
}