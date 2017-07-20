/**
 * 日期格式化方法
 * */
function formatTime(date) {
  var year = date.getFullYear()
  var month = date.getMonth() + 1
  var day = date.getDate()

  var hour = date.getHours()
  var minute = date.getMinutes()
  var second = date.getSeconds()

  return [year, month, day].map(formatNumber).join('/') + ' ' + [hour, minute, second].map(formatNumber).join(':')
}

function formatNumber(n) {
  n = n.toString()
  return n[1] ? n : '0' + n
}

/**
 * 获取电影点评星星数组
 * */
function convertToStarsArray(stars) {
  var array = []
  var num = stars.toString().substring(0,1)
  for(var i = 1;i<=5;i++){
    if(i<=num){
      array.push(1)
    }else{
      array.push(0)
    }
  }
  return array
}

module.exports = {
  formatTime: formatTime,
  convertToStarsArray: convertToStarsArray
}
