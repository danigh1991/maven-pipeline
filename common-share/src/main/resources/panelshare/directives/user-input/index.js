angular.module('emixApp.directives').directive('ngUserInput', ['sharedInfo', 'httpServices', '$rootScope', 'shareFunc', '$compile', '$timeout', '$translate',
    function (sharedInfo, httpServices, $rootScope, shareFunc, $compile, $timeout, $translate) {
        'use strict';

        return {
            scope: {
                options: '=',
                className: '=?',
                afterLoadData: '&',
                afterFailedData: '&',
                afterEnterEvent: '&'
            },
            templateUrl: '/panelshare/directives/user-input/template.html' + $rootScope.version,
            link: function (scope, element, attrs) {

                /*#region Utils*/
                scope.afterRestoreSavedState = function () {
                    $timeout(function () {
                        scope.changeInput();
                    });
                }
                var notValid = function (){
                    scope.options.userObj = null;
                    scope.options.userName = '';
                    scope.options.userId = '';
                    scope.options.validate = false;
                }
                /*#endregion*/

                /*#region Init Options*/
                scope.options.events = scope.options.events || {};
                scope.options.caption = scope.options.caption || 'common.userName';
                scope.className = scope.options.className || '';
                scope.options.required = scope.options.required === true;
                scope.options.showError = scope.options.showError === true;
                scope.options.form = scope.options.form || null;
                scope.options.validate = scope.options.validate || false;
                scope.options.disabled = scope.options.disabled === true;

                if(((typeof scope.options.userName) !== "undefined" && scope.options.userName !== "undefined" && scope.options.userName.length > 0) ||
                    (typeof scope.options.userId) !== "undefined" && scope.options.userId !== "undefined" && scope.options.userId.length > 0) {

                    $timeout(function () {
                        scope.loadData();
                    });

                }else{
                    notValid();
                }
                /*#endregion*/

                /*#region funcs*/
                scope.enterEventFire = false;
                scope.enterEvent = function () {
                    scope.enterEventFire = true;
                    scope.changeInput();
                }
                scope.changeInput = function () {
                    scope.options.userId = 0;
                    scope.loadData();
                }
                scope.loadData = function () {
                    if((typeof scope.options.userId) !== "undefined" && scope.options.userId !== "undefined" && scope.options.userId) {
                        /*NProgress.start();
                        $.blockUI();*/
                        httpServices.getUserInfoById(scope.options.userId)
                            .then(function (response) {
                                scope.options.userObj = response.data.returnObject;
                                scope.options.userName = response.data.returnObject.userName;
                                scope.options.validate = true;
                                scope.afterLoadData && scope.afterLoadData();
                                scope.enterEventFire && scope.afterEnterEvent && scope.afterEnterEvent();
                            }, function (response) {
                                notValid();
                                scope.afterFailedData && scope.afterFailedData();
                                httpServices.handleError(response.data, response.status, response.headers, response.config);
                            })
                            .finally(function () {
                                /*NProgress.done();
                                $.unblockUI();*/
                                scope.enterEventFire = false;
                            });
                    }else if((typeof scope.options.userName) !== "undefined" && scope.options.userName !== "undefined" && scope.options.userName.length > 0) {
                        /*NProgress.start();
                        $.blockUI();*/
                        httpServices.getUserInfoByUserName(scope.options.userName)
                            .then(function (response) {
                                scope.options.userObj = response.data.returnObject;
                                scope.options.userId = response.data.returnObject.id;
                                scope.options.validate = true;
                                scope.afterLoadData && scope.afterLoadData();
                                scope.enterEventFire && scope.afterEnterEvent && scope.afterEnterEvent();
                            }, function (response) {
                                notValid();
                                scope.afterFailedData && scope.afterFailedData();
                                httpServices.handleError(response.data, response.status, response.headers, response.config);
                            })
                            .finally(function () {
                                /*NProgress.done();
                                $.unblockUI();*/
                                scope.enterEventFire = false;
                            });
                    }else{
                        notValid();
                        scope.afterFailedData && scope.afterFailedData();
                        scope.enterEventFire = false;
                    }
                    
                }
                /*#endregion*/

                /*#region Events*/
                scope.options.events.loadData = function (){
                    $timeout(function () {
                        scope.loadData();
                    });
                }
                /*#endregion*/
            }
        }

    }]);

