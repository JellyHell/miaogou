var unit = unit || angular.module("unit", []);

/**
 * 面包屑导航操作对象service层
 */
unit.service("breakCrumbService", function() {

    // 面包屑导航对象
    var breakCrumb = [];

    // 回调方法
    var callbackFn = "";

    /**
     * 设置面包屑导航的值
     *
     * @param obj 当前点击的面包屑对象
     */
    this.setBreakCrumb = function(obj) {

        // 保存面包屑对象
        breakCrumb = obj;

        // 如果有回调方法，则执行回调方法
        if (callbackFn) {
            callbackFn();
        }
    };

    /**
     * 设置面包屑导航的值
     *
     * @param obj 当前点击的面包屑对象
     */
    this.appendBreakCrumb = function(obj) {

        // 保存面包屑对象
        breakCrumb.push(obj);

        // 如果有回调方法，则执行回调方法
        if (callbackFn) {
            callbackFn();
        }
    };

    /**
     * 获取面包屑导航的值
     *
     * @return {Object} 面包屑对象
     */
    this.getBreakCrumb = function() {
        return breakCrumb;
    };

    /**
     * 设置回调方法
     *
     * @param {Object} callback 回调方法
     */
    this.setCallBackFn = function(callback) {
        callbackFn = callback;
    };
});

/**
 * 获取操作记录
 */
unit.service("operBackupInfo", function($http, $config) {
	
	// 获取登录的用户信息
	var loginUser = JSON.parse(sessionStorage.getItem("loginUser"));
	
	/**
	 * 获取值
	 */
	this.getOperBackupInfo = function(userId) {
		return $http({
			url: $config.ip + "User/getOperBackupInfo",
			method: "GET",
			params: {
				userId: userId || loginUser.userId
			}
		});
	};
});