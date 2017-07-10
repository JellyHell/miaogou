// 获取或创建unit模块
var unit = unit || angular.module("unit", ["ngRoute"]);

/** 解决双向绑定输入中文失效的情况 */
unit.directive('ngAuto', function() {
    return {
        restrict: 'A',
        require: "ngModel",
        link: function(scope, element, attrs, $ngModel) {

            // 当元素执行键盘事件是触发
            element.bind('blur', function() {

                // 更改noModel的值
                $ngModel.$setViewValue(this.value);
                $ngModel.$render();

                // 刷新作用域
                scope.$apply();
            });
        }
    }
});

/** 解决输入框中只能输入数字或者. */
unit.directive('ngNumd', function() {
    return {
        restrict: 'A',
        require: "ngModel",
        link: function(scope, element, attrs, $ngModel) {

            // 当元素执行键盘事件是触发
            element.bind('input', function() {
                //先把非数字的都替换掉，除了数字和.
                var v = this.value.replace(/[^\d.]/g,"");
                //必须保证第一个为数字而不是.
                v  = v.replace(/^\./g,"");
                //保证只有出现一个.而没有多个.
                v  = v.replace(/\.{2,}/g,"");
                //保证.只出现一次，而不能出现两次以上
                v  = v.replace(".","$#$").replace(/\./g,"").replace("$#$",".");

                // 更改noModel的值
                $ngModel.$setViewValue(v);
                $ngModel.$render();

                // 刷新作用域
                scope.$apply();
            });
        }
    }
});

/** 时间选择器，且可以动态绑定angular的ngModel对象上 */
unit.directive('wdatePicker', function() {
    return {
        restrict: 'A',
        scope: {
            minDate: "=",
            maxDate: "=",
            fmt: "@"
        },
        require: "ngModel",
        link: function(scope, element, attrs, $ngModel) {

            // 当点击输入框的时候，加载日期组件
            element.bind("click, focus", function() {

                // 生成日期组件
                WdatePicker({
                	
                	// 格式化日期
                	dateFmt: scope.fmt || 'yyyy-MM-dd',
                	
                    // 最大值
                    maxDate: (scope.maxDate || ''),

                    // 最小值
                    minDate: (scope.minDate || ''),

                    // 每次选择时间组件的时候触发的时间
                    onpicking: function(dp) {

                        // 更改noModel的值
                        $ngModel.$setViewValue(dp.cal.getNewDateStr('yyyy-MM-dd'));
                        $ngModel.$render();

                        // 刷新作用域
                        scope.$apply();
                    },
                    onclearing: function(dp) {

                        // 更改noModel的值
                        $ngModel.$setViewValue("");
                        $ngModel.$render();

                        // 刷新作用域
                        scope.$apply();
                    }
                });
            })

        }
    }
});

/**
 * 表单验证
 */
unit.directive('validate', function() {
    return {
        restrict: 'A',
        scope: {
            type: "="
        },
        link: function(scope, element, attrs) {

        }
    }
});

/**
 * 弹框组件
 */
unit.factory("$model", function() {
    return {
        // 吐司弹框提示
        _toast: function (content, callback) {
            var $html = $('<div class="toast-mask"><div class="toast">'+ content +'</div></div>')
            $html.appendTo($("body")).fadeIn()
            setTimeout(function() {
                $html.fadeOut()
                callback && callback()
            }, 1000);
        },
    };
});