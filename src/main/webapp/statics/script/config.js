var unit = angular.module("unit", ["ngRoute"]);

/**
 * 配置全局需要的路由
 *
 * @param {Object} $routeProvider 路由对象
 */
unit.config(["$routeProvider", function ($routeProvider) {
    $routeProvider
        .when("/personDocument", {
            templateUrl: "./pages/document/personDocument.html",
            controller: "PersonDocumentController"
        })
        .when("/editDocument", {
            templateUrl: "./pages/document/editDocument.html",
            controller: "EditDocumentController"
        })
        .when("/staffRecord", {
            templateUrl: "./pages/document/staffRecord.html",
            controller: "StaffRecordController"
        })
        .when("/documentAudit", {
            templateUrl: "./pages/document/documentAudit.html",
            controller: "DocumentAuditController"
        })
        .when("/labelManager", {
            templateUrl: "./pages/document/labelManager.html",
            controller: "LabelManagerAuditController"
        })
        .when("/operationRecord", {
            templateUrl: "./pages/document/operationRecord.html",
            controller: "OperationRecordController"
        })
        .when("/staffDocument", {
            templateUrl: "./pages/document/staffDocument.html",
            controller: "StaffDocumentController"
        })
        .when("/performance/staffPerformance", {
            templateUrl: "./pages/performance/staffPerformance.html",
            controller: "StaffPerformanceController"
        })
        .when("/train/trainManager", {
        	templateUrl: "./pages/train/trainManager.html",
            controller: "TrainController"
        })
        .when("/attendance/self", {
        	templateUrl: "./pages/attendance/selfSignIn.html",
            controller: "TrainController"
        })
        .otherwise({redirectTo: '/personDocument'});
}]);

/**
 * 全局配置文件
 */
unit.factory("$config", function() {
	return {
		ip: "http://127.0.0.1:8080/zdoa/",
        mockIp: "/statics/mock/",
        defaultId: "SA201705231012"
	}
});