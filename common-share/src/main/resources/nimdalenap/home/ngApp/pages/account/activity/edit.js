angular.module('emixApp.controllers').controller('editactivity_index',
    ['$scope', 'httpServices', 'passObject', '$rootScope', '$filter', 'shareFunc', 'sharedInfo', '$modalInstance', '$translate',
        function ($scope, httpServices, passObject, $rootScope, $filter, shareFunc, sharedInfo, $modalInstance, $translate) {
            'use strict';

            $scope.showError = false;
            $scope.showBreadcrump = !passObject;
            $scope.query = '';
            $scope.isLeftToRight = siteConfig.isLeftToRight;

            $scope.getAllPermissions = function () {
                NProgress.start();
                $.blockUI();
                httpServices.getAllPermissions()
                    .then(function (response) {
                        var data = response.data;
                        $scope.permissions = data.returnObject;
                        $scope.getActivityInfo();
                    }, function (response) {
                        httpServices.handleError(response.data, response.status, response.headers, response.config);
                        NProgress.done();
                        $.unblockUI();
                    })
                    .finally(function () {

                    });
            }
            $scope.getAllPermissions();

            $scope.getActivityInfo = function () {

                httpServices.getActivityInfo(passObject.activityId)
                    .then(function (response) {
                        var data = response.data;
                        $scope.result = {
                            id: data.returnObject.id,
                            title: data.returnObject.text,
                            description: data.returnObject.description,
                            permissions: data.returnObject.permissions.map(function (value){ return value.id}),
                            active: data.returnObject.active,
                            icon: data.returnObject.icon,
                            blank: data.returnObject.isBlank,
                            modal: data.returnObject.isModal,
                        }
                    }, function (response) {
                        httpServices.handleError(response.data, response.status, response.headers, response.config);
                    })
                    .finally(function () {
                        NProgress.done();
                        $.unblockUI();
                    });
            }

            $scope.editActivity = function () {

                $scope.showError = true;
                if (!$scope.myForm.$valid) return;
                NProgress.start();
                $.blockUI();

                httpServices.editActivity($scope.result)
                    .then(function (response) {
                        var data = response.data;
                        toastr.success($translate.instant('common.successfullySavedMessage'));
                        sharedInfo.setNeedRefresh(true);
                        setTimeout($scope.closeModal(), 1000);
                    }, function (response) {
                        httpServices.handleError(response.data, response.status, response.headers, response.config);
                    })
                    .finally(function () {
                        NProgress.done();
                        $.unblockUI();
                    });
            }

            $scope.closeModal = function () {
                $modalInstance.dismiss('cancel');
            }

        }]);
