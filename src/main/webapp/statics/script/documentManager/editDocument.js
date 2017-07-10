var unit = unit || angular.module("unit", ["ngRoute"]);

/**
 * 编辑档案控制器
 *
 * @param {Object} $scope 全局控制器
 */
unit.controller("EditDocumentController", function($scope, $http, $config, operBackupInfo) {

	// 拷贝用户信息
	$scope.copyUserInfo = function() {

		// 如果用户信息不存在，则开始等待基本信息对象
		if (!$scope.userInfo) {
			var watchFn = $scope.$watch("userInfo", function(newValue, oldValue) {
				if (newValue) {
					
					// 将接口获取到的值放到编辑中
					$scope.userInfoEdit = $.extend(true, {}, $scope.userInfo);
					$scope.getOperBackupInfo();
					// 清除监听器
					watchFn();
				}
			});
			return;
		}

		// 如果值传过来了，则直接深拷贝
		$scope.userInfoEdit = $.extend(true, {}, $scope.userInfo);
		$scope.getOperBackupInfo();
	}
	
	// 拷贝在职信息
	$scope.copyJobInfo = function() {

		// 如果用户信息不存在，则开始等待基本信息对象
		if (!$scope.onJobInfo) {
			var watchFn = $scope.$watch("onJobInfo", function(newValue, oldValue) {
				if (newValue) {
					
					// 将接口获取到的值放到编辑中
					$scope.onJobInfoEdit = $.extend(true, {}, $scope.onJobInfo);
					
					// 清除监听器
					watchFn();
				}
			});
			return;
		}
		
		// 如果值传过来了，则直接深拷贝
		$scope.onJobInfoEdit = $.extend(true, {}, $scope.onJobInfo);
	};
	
	// 拷贝基本信息
	$scope.copyBasicInfo = function() {

		// 如果用户信息不存在，则开始等待基本信息对象
		if (!$scope.basicInfo) {
			var watchFn = $scope.$watch("basicInfo", function(newValue, oldValue) {
				if (newValue) {
					
					// 将接口获取到的值放到编辑中
					$scope.basicInfoEdit = $.extend(true, {}, $scope.basicInfo);
					
					// 清除监听器
					watchFn();
				}
			});
			return;
		}
		
		// 如果值传过来了，则直接深拷贝
		$scope.basicInfoEdit = $.extend(true, {}, $scope.basicInfo);
	};

	/**
	 * 获取备注信息
	 */
	$scope.getOperBackupInfo = function() {
		operBackupInfo.getOperBackupInfo().success(function(data) {
			if (data.errcode == 0 && data.data && data.data.length > 0) {
				$scope.backupInfo = data.data[data.data.length - 1].backupInfo;
			}
		});
	};

	/**
	 * 提交所有信息
	 */
	$scope.submitAllInfo = function() {

		// 展示正在上传动画
		$scope.isUploading = true;
		$scope.uploadMessage = "正在上传档案信息";

		// 先保存基本信息
		$http({
			url: $config.ip + "User/saveProfile",
			method: "POST",
			params: $scope.userInfoEdit
		}).success(function(data) {

			// 如果基本信息保存成功，则保存在职信息
			if (data.errcode == 0) {
				
				// 上传提示消息
				$scope.uploadMessage = "正在上传在职信息";

				$http({
					url: $config.ip + "User/saveJobInformation",
					method: "POST",
					params: $scope.onJobInfoEdit
				}).success(function(data) {
					if (data.errcode == 0) {
						
						// 上传提示消息
						$scope.uploadMessage = "正在上传备注信息";

						$http({
							url: $config.ip + "User/addOperBackupInfo",
							method: "POST",
							params: {
								userId: $scope.userInfoEdit.userId,
								backupInfo: $scope.backupInfo
							}
						}).success(function(data) {
							if (data.errcode == 0) {

								// 展示正在上传动画
								$scope.isUploading = false;

								// 提示保存成功
								$model._toast("保存成功");
							}
						});
					}
				})
			}
		})
	};

	$scope.copyUserInfo();
	$scope.copyJobInfo();
	$scope.copyBasicInfo();
});