var unit = unit || angular.module("unit", []);

/**
 * 头部菜单，可以展示头部的操作和下拉选项
 */
unit.directive("unitMenu", function() {
    return {
        restrict: "EAC",
        scope: {
        	userInfo: "="
        },
        replace: true,
        template: [
            '<header class="navbar navbar-static-top bs-docs-nav" id="top">',
            '<div class="container-fluid">',
            '<div class="navbar-header">',
            '<button class="navbar-toggle collapsed" type="button" data-toggle="collapse" data-target="#bs-navbar" aria-controls="bs-navbar" aria-expanded="false">',
            '<span class="sr-only">Toggle navigation</span>',
            '<span class="icon-bar"></span>',
            '<span class="icon-bar"></span>',
            '<span class="icon-bar"></span>',
            '</span>',
            '</button>',
            '<a href="../" class="logo">',
            '<i class="icon icon-logo"></i>',
            '</a>',
            '</div>',
            '<nav id="bs-navbar" class="collapse navbar-collapse">',
            '<ul class="nav navbar-nav nav-user">',
            '<li ng-repeat="menu in menuInfos" ng-class="{open: menu.open, active: menu.active}" ng-mouseover="menu.open = true" ng-mouseout="menu.open = false">',
            '<a class="bg menu" ng-href="{{menu.href}}" ng-click="setMenuItems(menu)"><i class="icon {{menu.icon}}"></i> {{menu.name}}</a>',
            '<ul class="dropdown-menu active" ng-if="menu.children && menu.children.length > 0">',
            '<li ng-repeat="child in menu.children" ng-click="setMenuItems(menu, child)">',
            '<a ng-href="{{child.href}}" class="active">',
            '<i class="active glyphicon glyphicon-play"></i> {{child.name}}',
            '</a>',
            '</li>',
            '</ul>',
            '</li>',
            '</ul>',
            '<ul class="nav navbar-nav navbar-right nav-user-right">',
            '<li ng-show="messageNumber && messageNumber > 0">',
            '<a class="menu-right" href="#" class="right-menu-text bg" target="_blank">',
            '<span class="envelope">（{{messageNumber}}）</span>',
            '<span class="icon icon-inline icon-envelope envelope"></span>',
            '</a>',
            '</li>',
            '<li>',
            '<a class="menu-right pt-25" href="#" class="right-menu-text bg" target="_blank">',
            '<img ng-src="{{userInfo.imgUrl}}" style="height: 28px;"> 欢迎你，{{userInfo.userName}}',
            '</a>',
            '</li>',
            '<li>',
            '<a class="menu-right" href="#" class="exit-menu-text bg" ng-click="exitSystem()">',
            '<i class="icon icon-exit"></i>',
            '</a>',
            '</li>',
            '</ul>',
            '</nav>',
            '</div>',
            '</header>'
        ].join(""),

        // 全局控制器
        controller: function($scope, breakCrumbService, $location, $config, $http, $model) {

            // 保存菜单
            $scope.menuInfos = menuInfos;

            // 当前人的代办信息条目
            $scope.messageNumber = 1;

            /**
             * 获取第一个面包屑导航
             */
            $scope.getFirstBreakCrumb = function() {

                // 第一次进入需要展示的面包屑导航
                var breakCrumb = [];

                // 如果当前是首页，则让面包屑展示首页
                if (!$location.path()) {
                    breakCrumb = $scope.menuInfos[0].children[0]?[$scope.menuInfos[0], $scope.menuInfos[0].children[0]]: [$scope.menuInfos[0]];

                    // 默认让面包屑导航展示第一集菜单
                    breakCrumbService.setBreakCrumb(breakCrumb);
                    return;
                }
            };

            /**
             * 设置菜单项
             *
             * @param {Object} firstMenu 一级菜单
             * @param {Object} secondMenu 二级菜单
             */
            $scope.setMenuItems = function(firstMenu, secondMenu) {

                // 设置面包屑导航
                breakCrumbService.setBreakCrumb(secondMenu?[firstMenu, secondMenu]:[firstMenu]);

                // 将二级菜单关闭
                firstMenu.open = false;

                // 遍历所有的菜单信息，将选中状态设置为false
                angular.forEach($scope.menuInfos, function(val, index) {
                    val.active = false;
                });

                // 将当前菜单设置为选中状态
                firstMenu.active = true;
            };

            /**
             * 退出系统
             */
            $scope.exitSystem = function() {

                // 如果用户id不存在，则直接退出
                if (!$scope.userInfo || !$scope.userInfo.userId) {
                    window.location.href = "/zdoa/statics/pages/login/login.html";
                    return;
                }

                $http({
                	url: $config.ip + "User/loginout",
                    method: "GET",
                    params: {
                        loginId: $scope.userInfo.userId
                    }
                }).success(function(data) {

                    // 如果推出成功，则跳转到推出成功页
                    if (data.errcode == 0) {
                    	$model._toast("退出成功", function() {
                    		window.location.href = "/zdoa/statics/pages/login/login.html";
                    	});
                    }
                });
            };
        }
    }
});


// 菜单项
var menuInfos = [{
    id: "home",
    icon: "icon-home",
    name: "首页",
    href: "javascript: void(0)",
    children: []
}, {
    id: "file",
    icon: "icon-file",
    name: "档案管理",
    href: "#/personDocument",
    children: [{
        id: "file1",
        name: "个人档案",
        href: "#/personDocument"
    }, {
        id: "file2",
        name: "员工档案",
        href: "#/staffRecord"
    }, {
        id: "file3",
        name: "档案审批",
        href: "#/documentAudit"
    }, {
        id: "file4",
        name: "标签管理",
        href: "#/labelManager"
    }]
}, {
    id: "performance",
    icon: "icon-performance",
    name: "绩效管理",
    href: "#/performance/staffPerformance",
    children: []
}, {
    id: "check",
    icon: "icon-check",
    name: "考勤管理",
    href: "javascript: void(0)",
    children: [{
        id: "file1",
        name: "个人考勤",
        href: "#/attendance/self"
    }, {
        id: "file2",
        name: "员工考勤",
        href: "#/attendance/staff"
    }]
}, {
    id: "train",
    icon: "icon-train",
    name: "培训管理",
    href: "#/train/trainManager",
    children: []
}, {
    id: "system",
    icon: "icon-system",
    name: "系统管理",
    href: "javascript: void(0)",
    children: [{
        id: "system1",
        name: "用户管理",
        href: "#"
    }, {
        id: "system2",
        name: "组织机构",
        href: "#"
    }, {
        id: "system3",
        name: "角色管理",
        href: "#"
    }, {
        id: "system4",
        name: "权限管理",
        href: "#"
    }, {
        id: "system5",
        name: "密码修改",
        href: "#"
    }, {
        id: "system6",
        name: "登录日志",
        href: "#"
    }]
}];