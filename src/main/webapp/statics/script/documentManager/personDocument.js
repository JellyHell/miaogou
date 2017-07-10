var unit = unit || angular.module("unit", []);

/**
 * 个人档案全局控制器
 *
 * @param {Object} $scope 全局作用域
 */
unit.controller("PersonDocumentController", function($scope, $http, breakCrumbService, $config) {

    // 点击编辑按钮
    $scope.editPersonInfo = function() {
        breakCrumbService.appendBreakCrumb({
            name: "编辑个人档案"
        })
    };

    // 点击操作记录按钮
    $scope.operationRecord = function() {
        breakCrumbService.appendBreakCrumb({
            name: "操作记录"
        })
    };
});