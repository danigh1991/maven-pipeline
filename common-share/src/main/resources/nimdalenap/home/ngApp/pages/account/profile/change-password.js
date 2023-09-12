angular.module('emixApp.controllers').controller('change_password_index',
    ['$scope', 'httpServices', '$routeParams', '$translate', '$timeout', 'shareFunc', function ($scope, httpServices, $routeParams, $translate, $timeout, shareFunc) {
        'use strict';

        $scope.isReset = $routeParams.isreset === '1';

        httpServices.getToken().then(function (response) {
            shareFunc.showMenu();
        }, function (response) {
            if(response.status === 405){
                shareFunc.hideMenu();
            }
        })

        $scope.changePassword = function () {
            $scope.showError = true;
            if (!$scope.myForm.$valid) {
                toastr.error($translate.instant('common.completeFormCarefully'));
                return;
            }
            if ($scope.password !== $scope.confirmPassword) {
                toastr.error($translate.instant('login.dontMatch'));
                return;
            }


            NProgress.start();
            $.blockUI();
            var moldPassword = ($scope.oldpassword == undefined ? '' : $scope.oldpassword);
            var _changePasswordInfo =
                {
                    newPassword: $scope.password,
                    confirmNewPassword: $scope.confirmPassword,
                    oldPassword: moldPassword
                }

            httpServices.changePassword(_changePasswordInfo)
                .then(function (response) {
                    var data = response.data;
                    toastr.success($translate.instant('common.successfullySavedMessage'));
                    $scope.password = '';
                    $scope.oldpassword = '';
                    $scope.confirmPassword = '';
                    $scope.showError = false;
                    httpServices.redirectWithDelay(Emix.Pages.changePassword, 100);
                    $timeout(function () {
                        location.reload();
                    }, 1000)
                }, function (response) {
                    httpServices.handleError(response.data, response.status, response.headers, response.config);
                })
                .finally(function () {
                    NProgress.done();
                    $.unblockUI();
                });

        }

        $scope.checkPassword = function () {
            $scope.myForm.confirmPassword.$error.dontMatch = $scope.password !== $scope.confirmPassword;
        };

    }]);

