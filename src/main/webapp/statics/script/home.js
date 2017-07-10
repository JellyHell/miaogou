var unit = unit || angular.module("unit", []);

/**
 * 首页控制器
 */
unit.controller("HomeController", function($scope, $http, $config) {
	
	// 当前登录的用户
	$scope.loginUser = JSON.parse(sessionStorage.getItem("loginUser") || "{}");
	
	// 加载个人详细数据
    $http({
        url: $config.ip + "User/getProfile",
        method: "GET",
        params: {
            userId: $scope.loginUser.userId
        }
    }).success(function(data) {

        // 如果返回报文成功，则直接展示
        if (data.errcode == 0) {
            $scope.userInfo = data.data;
        }
    });

    // 加载在职信息数据
    $http({
        url: $config.ip + "User/getJobInformation",
        method: "GET",
        params: {
            userId: $scope.loginUser.userId
        }
    }).success(function(data) {

        // 如果返回报文成功，则直接展示
        if (data.errcode == 0) {
            $scope.onJobInfo = data.data;
        }
    });
    
    // 加载基本信息数据
    $http({
        url: $config.ip + "User/getBasicInfo",
        method: "GET",
        params: {
            userId: $scope.loginUser.userId
        }
    }).success(function(data) {

        // 如果返回报文成功，则直接展示
        if (data.errcode == 0) {
            $scope.basicInfo = data.data;
        }
    });
});