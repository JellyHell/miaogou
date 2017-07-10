var unit = unit || angular.module("unit", ["ngRoute"]);

/**
 * 标签管理控制器
 */
unit.controller("LabelManagerAuditController", function($scope, $http, $config, $model) {
	
	// 从缓存中获取登录的信息
	$scope.loginUser = JSON.parse(sessionStorage.getItem("loginUser"));
	
	// 标签分类code
	$scope.tagClassCode = "";
	
	/**
	 * 获取全部标签信息
	 */
	$scope.getTagClass = function() {
		$http({
			url: $config.ip + "User/getTagClass",
			method: "GET"
		}).success(function(data) {
			
			// 请求接口成功
			if (data.errcode == 0) {
				$scope.tags = data.tagClassList;
			}
		});
	};
	
	/**
	 * 重命名标签分类
	 * 
	 * @param {Object} tag 标签对象
	 */
	$scope.renameTag = function(tag) {
		
		// 将标签置为可编辑状态
		tag.isEdit = true;
	};
	
	/**
	 * 新增一条标签分类
	 */
	$scope.addNewTag = function() {
		
		// 新增一条可编辑的
		$scope.tags.push({
			tagClassName: "",
			userId: $scope.loginUser.userId,
			isAdd: true
		});
	};
	
	/**
	 * 将新增加的标签发送给后台
	 * 
	 * @param {Object} tag 当前新增的标签
	 */
	$scope.addNewTagToRemote = function(tag) {
		
		// 如果当前是新增的，则用这个接口
		if (tag.isAdd) {
			$scope.url = "User/addTagClass";
			$scope.actionTxt = "已经成功新增了一条标签分类";
		}
		
		// 如果当前是编辑的，则用这个接口
		if (tag.isEdit) {
			$scope.url = "User/updateTagClass";
			$scope.actionTxt = "已经成功修改了一条标签分类";
		}
		
		// 请求接口
		$http({
			url: $config.ip + $scope.url,
			method: "POST",
			params: tag
		}).success(function(data) {
			
			// 请求接口成功
			if (data.errcode == 0) {
				
				// 弹出提示框
				$model._toast($scope.actionTxt);
				
				// 重新加载标签数据
				$scope.getTagClass();
			}
		});
	};
	
	/**
	 * 删除新增的标签
	 * 
	 * @param {Integer} $index 当前是第几个标签分类
	 */
	$scope.removeNewTagClass = function($index) {
		$scope.tags.splice($index, 1);
	};
	
	/**
	 * 移除指定的标签
	 * 
	 * @param {Object} tag 当前需要移除的标签
	 */
	$scope.removeTag = function(tag) {
		
		// 请求接口
		$http({
			url: $config.ip + "User/delTagClass",
			method: "GET",
			params: {
				tagClassCode: tag.code
			}
		}).success(function(data) {
			
			// 请求接口成功
			if (data.errcode == 0) {
				
				// 弹出提示框
				$model._toast("成功删除了一条标签分类");
				
				// 重新加载标签数据
				$scope.getTagClass();
			}
		});
	};
	
	/**
	 * 根据标签名称查找标签
	 */
	$scope.searchTagsByName = function() {
		
		// 请求接口
		$http({
			url: $config.ip + "User/getTagByTagClass",
			method: "GET",
			params: {
				tagClassCode: $scope.tagClassCode,
				tagName: $scope.tagName
			}
		}).success(function(data) {
			
			// 请求接口成功
			if (data.errcode == 0) {
				
				// 标签列表
				$scope.tagList = data.tagList;
			}
		});
	};
	
	/**
	 * 当前选中的标签分类
	 */
	$scope.selectTagClass = function(tag) {
		
		// 清空标签名称
		$scope.tagName = "";
		
		// 选中点击的标签
		$scope.tagClassCode = (tag.code || "");
		
		// 标签列表
		$scope.searchTagsByName();
	};
	
	/**
	 * 打开模态框，清空数据
	 * 
	 * @param {Integer} editType 当前类型 0：编辑 1：新增
	 * @param {Object} tag 可编辑的标签
	 */
	$scope.openModal = function(editType, tag) {
		
		// 设置当前是新增状态
		$scope.isEdit = (editType || 0);
		
		// 新增标签信息
		$scope.editTagInfo = $.extend({
			tagClassCode: $scope.tagClassCode,
			userId: $scope.loginUser.userId
		}, tag);
		
		// 展示模态框
		$("#myModal").modal("show");
	};
	
	/**
	 * 保存修改或新增的标签信息
	 * 
	 * @param {Integer} type 当前操作的类型 0：新增，1：编辑
	 */
	$scope.saveTagInfo = function(type) {
		
		// 默认是1编辑
		$scope.url = "User/updateTag";
		$scope.actionTxt = "修改标签成功";
		
		// 如果是新增
		if (type == 0) {
			$scope.url = "User/addTag";
			$scope.actionTxt = "新增标签成功";
		}
		
		// 请求接口
		$http({
			url: $config.ip + $scope.url,
			method: "POST",
			params: $scope.editTagInfo
		}).success(function(data) {
			
			// 请求接口成功
			if (data.errcode == 0) {
				
				// 弹框提示
				$model._toast($scope.actionTxt, function() {
					
					// 隐藏模态框
					$("#myModal").modal("hide");
				});
				
				// 标签列表
				$scope.searchTagsByName();
			}
		});
	};
	
	/**
	 * 删除标签信息
	 * 
	 * @param {Object} tag 可删除的标签
	 */
	$scope.deleteTagInfo = function(tag) {
		// 请求接口
		$http({
			url: $config.ip + "User/delTag",
			method: "GET",
			params: {
				code: tag.code
			}
		}).success(function(data) {
			
			// 请求接口成功
			if (data.errcode == 0) {
				
				// 弹框提示
				$model._toast("已成功删除了一条标签", function() {
					
					// 隐藏模态框
					$("#myModal").modal("hide");
				});
				
				// 标签列表
				$scope.searchTagsByName();
			}
		});
	};
	
	$scope.getTagClass();
	$scope.searchTagsByName();
});