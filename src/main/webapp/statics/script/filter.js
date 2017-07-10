var unit = unit || angular.module("unit", ["ngRoute"]);

/**
 * 自定义过滤器 性别
 */
unit.filter("sex", function() {
    return function(input) {
        if (input == 1) {
            return "男";
        }
        if (input == 2) {
            return "女";
        }
        return "--";
    }
});

/**
 * 自定义过滤器 政治面貌
 */
unit.filter("politics", function() {
    return function(input) {
        if (input == 0) {
            return "群众";
        }
        if (input == 1) {
            return "共青团员";
        }
        if (input == 2) {
            return "预备党员";
        }
        if (input == 3) {
            return "共产党员";
        }
        return "--";
    }
});

/**
 * 自定义过滤器 婚姻状况
 */
unit.filter("marrage_status", function() {
    return function(input) {
        if (input == 0) {
            return "未婚";
        }
        if (input == 1) {
            return "已婚";
        }
        return "--";
    }
});

/**
 * 自定义过滤器 生育情况状况
 */
unit.filter("bear_status", function() {
    return function(input) {
    	if (input == 0) {
            return "未生育";
        }
        if (input == 1) {
            return "已生育（一胎）";
        }
        if (input == 2) {
            return "已生育（二胎）";
        }
        if (input == 3) {
            return "已生育（三胎）";
        }
        if (input == 4) {
            return "已生育（四胎）";
        }
        return "--";
    }
});

/**
 * 弹出的tab页签名称
 */
unit.filter("tab_name", function() {
	return function(input) {
    	if (input == 0) {
            return "绩效考核";
        }
        if (input == 1) {
            return "教育经历";
        }
        if (input == 2) {
            return "资格证书";
        }
        if (input == 3) {
            return "岗位调整";
        }
        if (input == 4) {
            return "薪资变动";
        }
        if (input == 5) {
            return "工作经历";
        }
        if (input == 6) {
            return "培训经历";
        }
        if (input == 7) {
            return "其他附件";
        }
        return "--";
    }
});