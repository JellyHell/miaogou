var unit = unit || angular.module("unit", ["ngRoute"]);

/**
 * tab组件
 */
unit.directive("unitSigninTab", function() {
    return {
        restrict: "EAC",
        replace: true,
        scope: {
            type: "="
        },
        templateUrl: "components/unit-signin-tab/unitSigninTab.html",
        controller: "UnitSigninTabController"
    }
});

/**
 * 个人考勤tab页面
 */
unit.controller("UnitSigninTabController", function($scope, $http, $config, $model) {
	
	// 当前展示的页签
    $scope.$tabIndex = 0;
    
    /**
     * 点击tab页签后展示tab页签并且展示内容，和是否可编辑内容
     *
     * @param {Integer} $index 当前点击的第多少个tab页签
     * @param {Boolean} isForceLoadData 是否强制加载数据
     */
    $scope.showTabAndContent = function($index, isForceLoadData) {
    	
    	// 展示当前被点击的tab页签
        $scope.$tabIndex = $index;
    	
        // 展示当前tab页签下方的数据
        /*$scope.getTabDetailInfo($index, isForceLoadData);*/
    };
});