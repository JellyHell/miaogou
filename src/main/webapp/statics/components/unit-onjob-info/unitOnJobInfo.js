var unit = unit || angular.module("unit", ["ngRoute"]);

/**
 * 基本信息组件
 */
unit.directive("unitOnJobInfo", function() {
    return {
        restrict: "EAC",
        scope: {
            // 当前类型
            type: "=",
            onJobInfo: "=",
            basicInfo: "="
        },
        replace: true,
        templateUrl: "components/unit-onjob-info/unitOnJobInfo.html",
        controller: "UnitOnJobInfoController"
    }
});

/**
 * 基本信息组件控制器
 */
unit.controller("UnitOnJobInfoController", function($scope) {

    // 设置用户信息的基本类型（如果没穿，则默认是展示，非编辑）
    $scope.type = $scope.type || 1;
});