var unit = unit || angular.module("unit", ["ngRoute"]);

/**
 * tab组件
 */
unit.directive("unitTab", function() {
    return {
        restrict: "EAC",
        replace: true,
        scope: {
            type: "="
        },
        templateUrl: "components/unit-tab/unitTab.html",
        controller: "UnitTabController"
    }
});

/**
 * tab组件控制器
 */
unit.controller("UnitTabController", function($scope, $http, $config, $model, $timeout) {

    // 当前展示的页签
    $scope.$tabIndex = 0;

    // 当前查看的tab页签的类型（1：查看，2：编辑）
    $scope.type = $scope.type || 1;
    
    // 获取当前登录的用户信息
    $scope.loginUser = JSON.parse(sessionStorage.getItem("loginUser") || "{}");
    
    // 每个tab页需要请求的链接
    $scope.tabUrls = ["User/getperformanceAppraisal", "User/getEducationInfo", "User/getQualification", "User/getPostChange", "User/getMoneyChange", "User/getWorkExperience", "User/getTrain", "User/getOtherAttachment"];
    
    // 所有tab数据的集合
    $scope.tabInfos = [];

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
        $scope.getTabDetailInfo($index, isForceLoadData);
    };

    /**
     * 获取tab页签数据
     *
     * @param {Integer} $index 第几个页签
     * @param {Boolean} isForceLoadData 是否强制加载数据
     */
    $scope.getTabDetailInfo = function($index, isForceLoadData) {

        // 从缓存中获取表格信息
        $scope.tabInfos = JSON.parse(sessionStorage.getItem("tabInfos" + $scope.loginUser.userId) || "[]");

    	// 如果已经请求过数据，则不再请求数据
    	if ($scope.tabInfos[$index] && $scope.tabInfos[$index].length > 0 && !isForceLoadData) {
    		return;
    	}

        // 请求指定tab下的接口
    	$http({
    		url: $config.ip + $scope.tabUrls[$index],
    		method: "GET",
    		params: {
    			userId: $scope.loginUser.userId
    		}
    	}).success(function(data) {
    		
    		// 请求成功
    		if (data.errcode == 0) {
    			
    			// 将值赋值到tab中
    			$scope.tabInfos[$index] = data.list;

                // 将值保存到缓存中
    			sessionStorage.setItem("tabInfos" + $scope.loginUser.userId, JSON.stringify($scope.tabInfos));
    			
    			// 给该页签创建分页
    	        // $scope.createPage($index, 1, 1);
    		}
    	});
    };

    /**
     * 创建分页组件
     *
     * @param  {Integer} totalPage 总页数
     * @param  {Integer} current   当前页数
     * @return {[type]}           [description]
     */
    $scope.createPage = function($index, totalPage, current) {
        $($scope.getPageElementId($index)).createPage({
            pageCount:totalPage,
            current:current,
            backFn:function(p){
            	$scope.getTabDetailInfo($index);
            }
        });
    };
    
    /**
     * 获取分页对象id
     *
     * @param {Integer} $index 第几个分页对象
     */
    $scope.getPageElementId = function($index) {

        // 如果当前是待审核组件
        if ($scope.type == 3) {
            return "#tcdPageCode3" + $index;
        }

        return "#tcdPageCode" + $index;
    };
    
    /**
     * 保存新增的用户信息
     * 
     * @param {Integer} type 当前新增的类型
     */
    $scope.saveAddInfo = function(type) {

        // 待提交的表单对象
        var $formObj = "";

    	// 新增教育经历
    	if (type == 1) {
    		$formObj = $("#education").attr("action", $config.ip + "User/addEducationInfo");
    	}
    	
        // 新增资格证书
    	if (type == 2) {
    		$formObj = $("#qc").attr("action", $config.ip + "User/addQualification");
    	}

        // 增加工作经验
        if (type == 5) {
            $formObj = $("#we").attr("action", $config.ip + "User/addWorkExperience");
        }

        // 新增资格证书
        if (type == 7) {
            $formObj = $("#oa").attr("action", $config.ip + "User/addOtherAttachment");
        }

        // 提交form表单
        $formObj.ajaxSubmit({
            type: "POST",
            success: function(data) {
            	
            	// 提交成功
            	if (data.errcode == 0) {
            		
            		// 弹出提示信息
            		$model._toast("提交信息成功，系统会在1s后关闭此信息", function() {
            			
            			// 重置此表单
                		$formObj.resetForm();
                		
                		// 加载数据
                		$scope.showTabAndContent(type, true);
                		
                		// 关闭弹框
                		$("#myModal").modal("hide");
            		});
            		return;
            	}
            	
            	// 如果提交错误，提示错误信息
            	$model._toast(data.errmsg);
            }
        })
    };

    /**
     * 保存编辑的信息
     *
     * @param {Integer} type 当前新增的类型
     */
    $scope.saveEditInfo = function(type) {

        // 新增教育经历
        if (type == 1) {
            $scope.url = $config.ip + "User/updateEducationInfo";
        }
        
        // 新增资格证书
        if (type == 2) {
            $scope.url = $config.ip + "User/updateQualification";
        }

        // 增加工作经验
        if (type == 5) {
            $scope.url = $config.ip + "User/updateWorkExperience";
        }

        // 新增资格证书
        if (type == 7) {
            $scope.url = $config.ip + "User/updateOtherAttachment";
        }

        $http({
            url: $scope.url,
            method: "POST",
            params: $scope.editItem
        }).success(function(data) {
            if (data.errcode == 0) {
                $model._toast("编辑信息成功", function() {

                    // 隐藏弹出框
                    $("#myModal").modal("hide");

                    // 加载数据
                    $scope.showTabAndContent($scope.currentIndex, true);
                });
            }
        });
    };
    
    /**
     * 是否展示新增按钮
     */
    $scope.showAddBtn = function() {
        return $scope.type == 2 && $scope.$tabIndex != 0 && $scope.$tabIndex != 3 && $scope.$tabIndex != 4 && $scope.$tabIndex != 6;
    };

    /**
     * 获取当前时间
     */
    $scope.getCurrentDate = function() {
        return new Date().format("yyyy-MM-dd hh:mm:ss");
    };

    /**
     * 删除tab下的item
     *
     * @param {String} tabName 表明
     * @param {String} code 当前行的code
     * @param {Integer} $index 当前是第几个tab
     */
    $scope.deleteTabItem = function(tabName, code, $index) {
        $http({
            url: $config.ip + "User/delperProfileTabs",
            method: "GET",
            params: {
                code: code,
                tableName: tabName
            }
        }).success(function(data) {

            // 删除成功执行刷新操作
            if (data.errcode == 0) {
                $model._toast("数据删除成功，系统会在1s后刷新页面", function() {
                    // 加载数据
                    $scope.showTabAndContent($index, true);
                });
            }
        });
    };

    /**
     * 新增tab下的item
     *
     * @param {String} item 当前tab页的操作的数据
     * @param {String} $index 当前的状态
     * @param {Integer} $index 当前是第几个tab
     */
    $scope.addTabItem = function() {
    	
    	// 标记当前是新增状态
    	$scope.isEdit = 0;

        // 初始化编辑的item
        $scope.editItem = {};
    	
    	// 展示模态框
    	$("#myModal").modal("show");
    };
    
    /**
     * 编辑tab下的item
     *
     * @param {String} item 当前tab页的操作的数据
     * @param {String} $index 当前的状态
     * @param {Integer} $index 当前是第几个tab
     */
    $scope.editTabItem = function(item, $index) {
    	
    	// 深拷贝当前操作对象
    	$scope.editItem = $.extend(true, {}, item);
    	
    	// 标记当前是编辑状态
    	$scope.isEdit = 1;
        $scope.currentIndex = $index;
    	
    	// 展示模态框
    	$("#myModal").modal("show");
    };
    
    /**
     * 展示tab详情
     * 
     * @param {String} item 当前tab页的操作的数据
     */
    $scope.showTabItem = function(item) {
    	
    	// 深拷贝当前操作对象
    	$scope.editItem = $.extend(true, {}, item);
    	
    	// 只是展示用
    	$scope.isEdit = 1;
    	$scope.onlyShow = true;
    	
    	// 展示模态框
    	$("#myModal").modal("show");
    };
    
    /**
     * 删除已经上传的附件
     * 
     * @param {String} fileCode 文件的code
     * @param {String} tabCode
     */
    $scope.deleteUploadFile = function(fileCode, tabCode) {
    	$http({
            url: $config.ip + "User/delAttachment",
            method: "GET",
            params: {
                code: fileCode,
                tabCode: tabCode
            }
        }).success(function(data) {

            // 删除附件成功，调用接口
            if (data.errcode == 0) {
                $model._toast("删除附件成功", function() {
                    
                    // 保存附件
                    $scope.editItem.attachmentList = data.attachmentList;
                    
                    // 加载数据
                    $scope.showTabAndContent($scope.currentIndex, true);
                })
            }
        });
    };

    // 对输入框绑定事件
    $timeout(function() {
        $("input[type=file]").change(function() {
            if ($scope.isEdit == 1 && $(this).val()) {
                var $this = $(this);
                $this.parents("form").attr("action", $config.ip + "User/addAttachment").ajaxSubmit({
                    type: "POST",
                    success: function(data) {
                        
                        // 清空值
                        $this.value = "";

                        // 保存附件
                        $scope.editItem.attachmentList = data.attachmentList;

                        // 加载数据
                        $scope.showTabAndContent($scope.currentIndex, true);
                    }
                })
            }
        });
    });
    
    $scope.getTabDetailInfo(0);
});