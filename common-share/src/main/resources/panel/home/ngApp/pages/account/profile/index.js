angular.module('emixApp.controllers').controller('profile_index',
    ['$scope', 'httpServices', '$rootScope', 'sharedInfo', 'shareFunc', 'configService', '$translate',
        function ($scope, httpServices, $rootScope, sharedInfo, shareFunc, configService, $translate) {
        'use strict';

        $scope.result = "";
        $scope.configs = {};
        configService.then(function (options) {
            $scope.configs = options;
        });

        $scope.userPic = Emix.Api.Account.image + sharedInfo.getUserLogoPath();
        $scope.gendersList = sharedInfo.getGendersList();
        $scope.projectType=httpServices.getProjectType();

        /*$scope.getCityList = function () {
            NProgress.start();
            $.blockUI();
            httpServices.getCityList()
                .success(function (data, status, headers, config) {
                    $scope.cities = data.returnObject;
                    $scope.selectedCity = $scope.cities[0].id;
                    $scope.getUserInfo();
                })
                .error(function (data, status, headers, config) {
                    httpServices.handleError(data, status, headers, config);
                })
                .finally(function () {
                    NProgress.done();
                    $.unblockUI();
                });
        }

        $scope.getCityList();*/

        $scope.getUserInfo = function () {
            NProgress.start();
            $.blockUI();

            httpServices.getUserInfo()
                .then(function (response) {
                    var data = response.data;
                    $scope.result = data.returnObject;
                    $scope.diableAffiliateReagent = $scope.result.affiliateReagent && $scope.result.affiliateReagent.length > 0;
                }, function (response) {
                    httpServices.handleError(response.data, response.status, response.headers, response.config);
                })
                .finally(function () {
                    NProgress.done();
                    $.unblockUI();
                });
        }
        $scope.getUserInfo();

        $scope.updateUserInfo = function () {

            $scope.showError = true;

            if (!$scope.myForm.$valid) {
                toastr.error($translate.instant('common.completeFormCarefully'));
                return;
            }

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
                    affiliateReagent: $scope.result.affiliateReagent
                }

            httpServices.updateUserInfo(_userInfo)
                .then(function (response) {
                    var data = response.data;

                    toastr.success($translate.instant('common.successfullySavedMessage'));
                    sharedInfo.saveUserData($scope.result.firstName + ' ' + $scope.result.lastName);
                    $rootScope.$emit("CallUpdateInfo", {});
                    $scope.getUserInfo();
                }, function (response) {
                    httpServices.handleError(response.data, response.status, response.headers, response.config);
                })
                .finally(function () {
                    NProgress.done();
                    $.unblockUI();
                });
        }

        $scope.changeUserAddress = function () {
            /*httpServices.redirect(Emix.Pages.userAddress);*/
            var passObject = {
                mode: 1//1 => User Address List, 2=> Change Shipment Address
            };
            shareFunc.openModal(passObject, '/panel/home/ngApp/pages/account/address/index.html', 'useraddresslist_index', 'x-lg',
                function (result) {
                }, function () {
                });
        }

        //#region User Image
        $scope.changeUserImage = function () {
            var passObject = {
                formData: {id: $scope.result.id},
                ratioWidth: 100,
                ratioHeight: 100,
                uploadUrl: Emix.Api.Profile.uploadProfileImage
            };
            shareFunc.openModal(passObject, '/nimdalenap/home/ngApp/pages/application/common/image/index.html', 'selectimage_index', 'md',
                function (result) {
                    sharedInfo.saveUserLogoPath(result);
                    $scope.userPic = Emix.Api.Account.image + sharedInfo.getUserLogoPath();
                    $('#userpic').attr("src", $scope.userPic);
                    $('#userpic').attr("ng-src", $scope.userPic);
                    $('#userpicheader').attr("src", $scope.userPic);
                    $('#userpicheader').attr("ng-src", $scope.userPic);

                }, function () {
                });
        }
        //#endregion


    }]);

