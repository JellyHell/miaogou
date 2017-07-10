var unit = unit || angular.module("unit", []);

/**
 * 员工档案详情页面
 */
unit.controller("StaffDocumentController", function($scope) {

    // 当前是待审核
    $scope.state = 1;

    // 当前是否处于审核状态下
    $scope.audit = true;
});