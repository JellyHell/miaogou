// 获取或创建unit模块
var unit = unit || angular.module("unit", []);

/**
 * 面包屑导航
 */
unit.directive("breakCrumb", function() {
    return {
        restrict: "EAC",
        replace: true,
        template: [
            '<ol class="breadcrumb">',
            '<li>当前：</li>',
            '<li ng-repeat="bc in breakCrumbs" ng-class="{active: $last}">' +
            '<span ng-if="$last">{{bc.name}}</span>' +
            '<a ng-href="{{bc.href}}" ng-if="!$last" ng-click="removeBreakCrumb($index)">{{bc.name}}</a>' +
            '</li>',
            '</ol>'
        ].join(""),

        // 全局控制器
        controller: function($scope, breakCrumbService) {

            /**
             * 从服务中获取面包屑导航数据，展示在页面上
             */
            $scope.setBreakCrumbToHTMl = function() {

                // 默认从服务中获取面包屑导航
                $scope.breakCrumbs = breakCrumbService.getBreakCrumb();
            };

            $scope.setBreakCrumbToHTMl();

            /**
             * 当点击面包屑导航时，将数据从面包屑导航中移除
             *
             * @param {Integer} $index 当前点击的第多少个，以此为长度，清除后面的数据
             */
            $scope.removeBreakCrumb = function($index) {

                // 如果是第一个，则不操作
                if ($index == 0) {
                    return;
                }

                // 删除后面的数据
                $scope.breakCrumbs.length = ($index + 1);
            };

            // 设置回调方法
            breakCrumbService.setCallBackFn($scope.setBreakCrumbToHTMl);
        }
    }
});