var unit = unit || angular.module("unit", ["ngRoute"]);

/**
 * 基本信息组件
 */
unit.directive("unitBasicInfo", function() {
    return {
        restrict: "EAC",
        scope: {
            // 当前类型
            type: "=",
            userInfo: "="
        },
        replace: true,
        templateUrl: "components/unit-basic-info/unitBasicInfo.html",
        controller: "UnitBasicInfoController"
    }
});

/**
 * 基本信息组件控制器
 */
unit.controller("UnitBasicInfoController", function($scope, $timeout, $config, $model) {

    // 设置用户信息的基本类型（如果没穿，则默认是展示，非编辑）
    $scope.type = $scope.type || 1;

    $timeout(function() {
        // 文件改变了
        $("input[type=file]").change(function() {
            if ($(this).val()) {
                $("#userPhoto").attr("action", $config.ip + "User/uploadHeadImg").ajaxSubmit({
                    type: "POST",
                    success: function(data) {
                        if (data.errcode == 0) {
                            // 当前上传的文件
                            $scope.userInfo.imgUrl = data.imgUrl;
                            $scope.$apply();
                        	$model._toast("头像上传成功");
                        }
                    }
                })
            }
        });
    });   
});