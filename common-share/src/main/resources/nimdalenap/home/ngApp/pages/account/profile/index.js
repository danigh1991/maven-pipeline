angular.module('emixApp.controllers').controller('profile_index',
    ['$scope', 'httpServices', '$rootScope', 'sharedInfo', '$translate', '$routeParams', 'shareFunc',
        function ($scope, httpServices, $rootScope, sharedInfo, $translate, $routeParams, shareFunc) {
            'use strict';

            $scope.userId = $routeParams.userId;
            $scope.userPic = Emix.Api.Account.image + '/u/' + $scope.userId + '?v=' + Date.now();

            if (!(jQuery.cookie('accessToken') === undefined && $("body").attr("class") === 'skin-blue ng-scope')) {
                jQuery('.sidebar-toggle').show();
                //jQuery('.sidebar-toggle').click();
                jQuery('.navbar-custom-menu').show();
            }

            $scope.loginTypeList = sharedInfo.getLoginType();

            $scope.result = "";

            /*#region Account Info*/
            $scope.params = {};

            function getAccountInfo() {
                httpServices.getAccountInfoByUserId($scope.userId)
                    .then(function (response) {
                        var data = response.data;
                        $scope.params.accounts = [data.returnObject];

                    }, function (response) {
                        httpServices.handleError(response.data, response.status, response.headers, response.config);
                    })
                    .finally(function () {
                        NProgress.done();
                        $.unblockUI();
                    });
            }

            $scope.userRoles = {selected: []};

            function getAllRoles() {
                NProgress.start();
                $.blockUI();
                httpServices.getAllRoles()
                    .then(function (response) {
                        var data = response.data;
                        $scope.allRoles = data.returnObject;
                        $scope.getUserInfoById();
                    }, function (response) {
                        httpServices.handleError(response.data, response.status, response.headers, response.config);
                        NProgress.done();
                        $.unblockUI();
                    })
                    .finally(function () {

                    });
            }

            getAllRoles();

            $scope.getUserInfoById = function () {

                httpServices.getUserInfoById($scope.userId)
                    .then(function (response) {
                        var data = response.data;
                        $scope.result = data.returnObject;
                        $scope.userRoles.selected = $scope.result.roles.map(function (role) {
                            return role.id
                        });
                        getAccountInfo();
                    }, function (response) {
                        httpServices.handleError(response.data, response.status, response.headers, response.config);
                        NProgress.done();
                        $.unblockUI();
                    })
                    .finally(function () {
                    });
            }

            $scope.editFull = function () {

                $scope.showError = true;

                if (!$scope.myForm.$valid) return;

                NProgress.start();
                $.blockUI();

                var _userInfo =
                    {
                        id: $scope.result.id,
                        firstName: $scope.result.firstName,
                        lastName: $scope.result.lastName,
                        mobileNumber: $scope.result.mobileNumber,
                        email: $scope.result.email,
                        //cityId: $scope.result.city.id,
                        address: $scope.result.address,
                        gender: $scope.result.gender,
                        sendNotify: $scope.result.sendNotify,
                        sendSms: $scope.result.sendSms,
                        sendEmail: $scope.result.sendEmail,
                        affiliateReagent: $scope.result.affiliateReagent,
                        roles: $scope.userRoles.selected,
                        enable: $scope.result.enabled,
                        expireDate: $scope.result.expireDate,
                        loginType: $scope.result.loginType,
                        limitIpList: $scope.result.limitIpList
                    }

                httpServices.editFull(_userInfo)
                    .then(function (response) {
                        var data = response.data;
                        toastr.success($translate.instant('common.successfullySavedMessage'));
                        sharedInfo.saveUserData($scope.result.firstName + ' ' + $scope.result.lastName);
                        $rootScope.$emit("CallUpdateInfo", {});
                    }, function (response) {
                        httpServices.handleError(response.data, response.status, response.headers, response.config);
                    })
                    .finally(function () {
                        NProgress.done();
                        $.unblockUI();
                    });
            }

            $scope.lockUser = function () {
                NProgress.start();
                $.blockUI();

                httpServices.lockUser($scope.result.id)
                    .then(function (response) {
                        var data = response.data;
                        toastr.success($translate.instant('common.successfullyActionMessage'));
                        $scope.getUserInfoById();
                    }, function (response) {
                        httpServices.handleError(response.data, response.status, response.headers, response.config);
                    })
                    .finally(function () {
                        NProgress.done();
                        $.unblockUI();
                    });
            }

            $scope.unlockUser = function () {
                NProgress.start();
                $.blockUI();

                httpServices.unlockUser($scope.result.id)
                    .then(function (response) {
                        var data = response.data;
                        toastr.success($translate.instant('common.successfullyActionMessage'));
                        $scope.getUserInfoById();
                    }, function (response) {
                        httpServices.handleError(response.data, response.status, response.headers, response.config);
                    })
                    .finally(function () {
                        NProgress.done();
                        $.unblockUI();
                    });
            }

            $scope.resetUserPassword = function () {
                NProgress.start();
                $.blockUI();

                httpServices.resetUserPassword($scope.result.id)
                    .then(function (response) {
                        var data = response.data;
                        toastr.success(data.message);
                    }, function (response) {
                        httpServices.handleError(response.data, response.status, response.headers, response.config);
                    })
                    .finally(function () {
                        NProgress.done();
                        $.unblockUI();
                    });
            }

            //#region LimitIp
            $scope.showLimitIpError = false;
            $scope.limitIp = '';
            $scope.validateIp = true;

            $scope.addLimitIp = function () {
                $scope.showLimitIpError = true;
                if($scope.limitIp) {
                    $scope.validateIp = shareFunc.validateIp($scope.limitIp);
                    if($scope.validateIp) {
                        var index = $scope.result.limitIpList.indexOf($scope.limitIp);
                        if (index >= 0) {
                            toastr.error($translate.instant('common.duplicateElement2', {'element': $translate.instant('common.value')}));
                            return;
                        }
                        $scope.result.limitIpList.push($scope.limitIp);
                        $scope.showLimitIpError = false;
                        $scope.validateIp = true;
                        $scope.limitIp = '';
                    }
                }
            }
            $scope.removeLimitIp = function (value) {
                var index = $scope.result.limitIpList.indexOf(value);
                $scope.result.limitIpList.splice(index, 1);
            }
            //#endregion
            //#region upload image utils
            $scope.maxImageUploadCount = 1;
            $scope.storeLogoList = [];
            $scope.selectImage = function () {
                var passObject = {
                    formData: {id: $scope.userId},
                    ratioWidth: 200,
                    ratioHeight: 200,
                    uploadUrl: Emix.Api.Profile.uploadProfileImage
                };
                shareFunc.openModal(passObject, '/nimdalenap/home/ngApp/pages/application/common/image/index.html', 'selectimage_index', 'md',
                    function (result) {
                        //$scope.getUserInfoById();
                        $scope.userPic = Emix.Api.Account.image + '/u/' + $scope.userId + '?v=' + Date.now();
                    }, function () {
                    });
            }
            //#endregion

        }]);

