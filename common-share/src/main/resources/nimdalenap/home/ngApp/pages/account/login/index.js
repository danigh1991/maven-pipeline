angular.module('emixApp.controllers').controller('login_index',
    ['$http', '$scope', '$rootScope', 'httpServices', 'sharedInfo', '$cookies', '$location', '$translate', '$interval', '$timeout', 'authService', 'shareFunc',
        function ($http, $scope, $rootScope, httpServices, sharedInfo, $cookies, $location, $translate, $interval, $timeout, authService, shareFunc) {
            'use strict';

            NProgress.done();
            $.unblockUI();

            $scope.title = new Date();
            $scope.userName = "";
            $scope.password = "";
            $scope.showError = false;

            var setFocusAfter = 500;//milisecond
            $scope.loginFormStatus = 'init';
            $scope.Timer = null;
            var initVariables = function () {
                $scope.sendOtpButtonText = $translate.instant('login.sendOtp');
                $scope.sendOtpButtonDisabled = false;
                $scope.sendOtpButtonDisableTime = 120;//Second
            }
            var setVariables = function (currentState) {
                if(currentState) {
                    var remainTime = parseInt(currentState.remainTime, 10);
                    $scope.sendOtpButtonDisabled = remainTime > 0;
                    $scope.sendOtpButtonDisableTime = remainTime;//Second
                }
            }
            $timeout(initVariables(), 100);

            $scope.nextFocus = function (nextId) {
                document.getElementById(nextId).focus();
            };

            $scope.sendOtpCode = function () {
                if(($scope.loginFormStatus==='password' || $scope.loginFormStatus==='otpPassword') && (!$scope.password || $scope.password.trim().length == 0)){
                    toastr.error($translate.instant('validate.requiredMessage', {'element': $translate.instant('common.password')}));
                    return;
                }
                var c2faRequest = {
                    userName: $scope.userName,
                    password: $scope.password,
                };
                $scope.send2faCode(c2faRequest);
            }

            var setVerificationTimeOnForm = function(){
                $scope.sendOtpButtonDisableTime -= 1;
                $scope.sendOtpButtonText = $translate.instant('login.sendOtp') + ' ' + $scope.sendOtpButtonDisableTime;
                if($scope.sendOtpButtonDisableTime <= 0){
                    initVariables();
                    if (angular.isDefined($scope.Timer)) {
                        $interval.cancel($scope.Timer);
                    }
                }
            }

            $scope.send2faCode = function (c2faRequest) {

                if (!$scope.myForm.$valid && !c2faRequest) {
                    $scope.showError = true;
                    toastr.error($translate.instant('common.completeFormCarefully'));
                    return;
                }

                if(!c2faRequest)
                    c2faRequest = {
                        userName: $scope.userName,
                    };

                NProgress.start();
                $.blockUI();

                httpServices.send2faCode(c2faRequest)
                    .then(function (response) {
                        $scope.showError = false;
                        if(response.data.returnObject.status == 'uniqueCode'){
                            return;
                        }

                        $scope.loginFormStatus = response.data.returnObject.status;
                        if($scope.loginFormStatus.toLowerCase() !== 'sendverificationcode') {
                            $scope.password = '';
                            $timeout(function () {
                                $('#password').focus();
                            }, setFocusAfter);
                        }
                        else{
                            toastr.success(response.data.returnObject.message);
                            $timeout(function () {
                                $('#otp').focus();
                            }, setFocusAfter);
                            //lock Send Otp Button
                            setVariables(response.data.returnObject);
                            setVerificationTimeOnForm();
                            if (angular.isDefined($scope.Timer)) {
                                $interval.cancel($scope.Timer);
                            }
                            $scope.Timer = $interval(function () {
                                setVerificationTimeOnForm();
                            }, 1000);
                        }
                    }, function (response) {
                        httpServices.handleError(response.data, response.status, response.headers, response.config);
                        //$scope.getCaptcha();
                    })
                    .finally(function () {
                        NProgress.done();
                        $.unblockUI();
                    });
            };

            $scope.login = function () {

                $scope.showError = true;
                if (!$scope.myForm.$valid) {
                    toastr.error($translate.instant('common.completeFormCarefully'));
                    return;
                }

                NProgress.start();
                $.blockUI();
                httpServices.login($scope.userName, $scope.password, $scope.otp,
                    '', '', '')
                    .then(function (response) {
                        var data = response.data;
                        sharedInfo.saveUserData(data.name + ' ' + data.family);

                        sharedInfo.saveUserId(data.userId);

                        sharedInfo.saveUserLogoPath(data.logoPath);

                        authService.setRefreshToken(data.refresh_token);

                        authService.setTokenInStorage(data.access_token);

                        //var date = new Date();
                        //var minutes = 30;
                        //date.setTime(date.getTime() + (minutes * 60 * 1000));
                        //$.cookie('accessToken', data.access_token, { expires: date, path: '/' });

                        loginSuccess();

                    }, function (response) {
                        httpServices.handleError(response.data, response.status, response.headers, response.config);
                        //$scope.getCaptcha();
                    })
                    .finally(function () {
                        NProgress.done();
                        $.unblockUI();
                    });
            };

            $scope.backToUserName = function () {
                $scope.loginFormStatus = 'init';
                $timeout(function () {
                    $('#userName').focus();
                }, setFocusAfter);
            }

            shareFunc.hideMenu();

            var loginSuccess = function () {
                if(httpServices.getPanelType() === 'admin')
                    adminPanelLoginSuccess();
                else
                    userPanelLoginSuccess();
            }
            var adminPanelLoginSuccess = function () {
                httpServices.redirect(Emix.Pages.dashboard);
                //$("body").attr('class', 'skin-blue ng-scope');
                shareFunc.showMenu();
            }

            var userPanelLoginSuccess = function () {
                var siteLoginPanel = $('div.navbar-login-box');
                var cityName = '';

                var redirectUrl = '';
                if (httpServices.getProjectType() === 'advertise') {
                    cityName = $location.absUrl().split('/')[3];
                    redirectUrl = (cityName.length > 0 ? '/' : '') + cityName + Emix.Pages.sitePanelPage;
                }

                if (siteLoginPanel.length > 0) {
                    redirectUrl = (cityName.length > 0 ? '/' : '') + cityName + Emix.Pages.sitePanelPage;
                } else
                    redirectUrl = cityName + Emix.Pages.dashboard;

                httpServices.redirect(redirectUrl);

                /*var $body = $("body");
                $body.removeClass('sidebar-collapse');
                $body.addClass('sidebar-open');*/
                shareFunc.showMenu();

                window.location.reload();
            }

            httpServices.checkIsLogin(function () {
                loginSuccess();
            });

            $scope.registershow = function () {
                httpServices.registershow();
            };
            // $scope.getCaptcha();

        }]);
