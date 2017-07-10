var unit = unit || angular.module("unit", []);

/**
 * 登录控制器
 */
unit.controller("LoginController", function($scope, $config, $http, $model) {

    /**
     * 点击登录
     */
    $scope.submitLoginUser = function() {

        // 未填写用户名
        if (!$scope.userName) {
            $model._toast("请填写用户名");
            return;
        }

        // 未填写密码
        if (!$scope.password) {
            $model._toast("请填写密码");
            return;
        }

        $http({
            url: $config.ip + "User/login",
            method: "GET",
            params: {
                loginId: $scope.userName,
                passWord: $scope.password
            }
        }).success(function(data) {

            // 如果登录成功，则将用户id保存到缓存中
            if (data.errcode == 0) {
                sessionStorage.setItem("loginUser", JSON.stringify(data.user));
                window.location.href = "../../";
                return;
            }
        });
    };
});