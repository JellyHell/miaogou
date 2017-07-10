var unit = unit || angular.module("unit", ["ngRoute"]);

/**
 * 操作记录控制器
 */
unit.controller("OperationRecordController", function($scope, operBackupInfo) {
	operBackupInfo.getOperBackupInfo().success(function(data) {
		if (data.errcode == 0) {
			$scope.backupInfos = data.data;
		}
	});
});