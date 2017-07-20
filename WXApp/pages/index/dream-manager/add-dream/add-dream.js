// add-dream.js
Page({

  /**
   * 页面的初始数据
   */
  data: {
    productImgs:[],
    addImgUri: "/images/avatar/addimg.png"
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    var images = []
    images.push(this.data.addImgUri)

    this.setData({
      productImgs: images
    })
  },
  
  /**
   * 图片点击
   */
  onImageTap:function(event){
    var selecteImgUri = event.currentTarget.dataset.img
  
    var that = this
    if (selecteImgUri == this.data.addImgUri){//添加照片
        var images = this.data.productImgs.slice(0, this.data.productImgs.length - 1)
        wx.chooseImage({
          count: 3 - images.length,
          sizeType: 'compressed',
          success: function(res) {
            for(var i = 0;i<res.tempFilePaths.length;i++){
               images.push(res.tempFilePaths[i])
            }

            if (images.length < 3){
              images.push(that.data.addImgUri)
            }

            that.setData({
              productImgs: images
            })
          },
        })
    }else{//浏览图片
      wx.previewImage({
        current: selecteImgUri, 
        urls: [selecteImgUri]
      })
    }
  },

  /**
   * 删除图片
   */
  onDeleteTap:function(event){
    var index = event.target.dataset.index
    var selectedImgs = this.data.productImgs
    var imgs = []
    if (selectedImgs[selectedImgs.length - 1] == this.data.addImgUri){
      imgs = selectedImgs.slice(0, selectedImgs.length - 2)
    }else{
      imgs = selectedImgs.slice(0, selectedImgs.length - 1)
    }
    imgs = imgs.splice(index,1)
    imgs.push(this.data.addImgUri)

    this.setData({
      productImgs: imgs
    })
  }
})