var unit = unit || angular.module("unit", ["ngRoute"]);

/**
 * 全局$http请求
 */
unit.config(function ($httpProvider) {
    $httpProvider.interceptors.push(function ($model) {
        return {
            'response': function (config) {

                // 将得到的错误码转码
                var errorcode = parseInt(config.data.errcode, 10);

                // 如果返回报文出错，则提示错误信息
                if (config.status == 200 && ! isNaN(errorcode) && errorcode != 0) {
                    $model._toast(config.data.errmsg);
                }

                return config;
            },
            'request': function(config) {

                // 获取登录的用户
                var loginUser = JSON.parse(sessionStorage.getItem("loginUser") || "{}");

                // 如果从缓存中获取用户信息不存在，则跳转到登录页面
                if (! loginUser || ! loginUser.userId) {

                    // 吐司弹框后，跳转到登录页面
                    $model._toast("未获取到授权的用户信息，请登录", function() {
                        window.location.href = "/zdoa/statics/pages/login/login.html";
                    });
                    return;
                }

                return config;
            }
        };
    });
});