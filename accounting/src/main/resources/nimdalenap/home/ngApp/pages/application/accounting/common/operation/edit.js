angular.module('emixApp.controllers').controller('editOperationtype_index',
    ['$scope', 'httpServices', 'passObject', '$rootScope', '$filter', 'shareFunc', 'sharedInfo', '$modalInstance', '$translate',
        function ($scope, httpServices, passObject, $rootScope, $filter, shareFunc, sharedInfo, $modalInstance, $translate) {
            'use strict';

            $scope.showError = false;
            $scope.showBreadcrump = !passObject;
            $scope.wageTypeList = sharedInfo.getWageTypeList();
            $scope.operationSourceTypeList = sharedInfo.getOperationSourceTypeList();

            $scope.getOperationTypeInfo = function () {
                NProgress.start();
                $.blockUI();
                httpServices.getOperationTypeInfo(passObject.operationTypeId)
                    .then(function (response) {
                        var data = response.data;
                        $scope.result = {
                            id: data.returnObject.id,
                            name: data.returnObject.name,
                            description: data.returnObject.description,
                            active: data.returnObject.active,
                            minAmount: data.returnObject.minAmount,
                            maxAmount: data.returnObject.maxAmount,
                            globalMaxDailyAmount: data.returnObject.globalMaxDailyAmount,
                            wageType: data.returnObject.wageType,
                            wageRate: data.returnObject.wageRate,
                            wageAmount: data.returnObject.wageAmount,
                            order: data.returnObject.order,
                            defaultAmounts: data.returnObject.defaultAmountList,
                            notify: data.returnObject.notify,
                        }
                        $scope.readOnlyResult = {
                            code: data.returnObject.code,
                            operationType: data.returnObject.operationType,
                            sourceType: data.returnObject.sourceType,
                            starter: data.returnObject.starter
                        }

                    }, function (response) {
                        httpServices.handleError(response.data, response.status, response.headers, response.config);
                    })
                    .finally(function () {
                        NProgress.done();
                        $.unblockUI();
                    });
            }

            $scope.getOperationTypeInfo();

            $scope.ok = function () {
                $scope.editOperationType();
            }

            $scope.editOperationType = function () {

                $scope.showError = true;

                if (!$scope.myForm.$valid) return;

                if (
                    $scope.result.minAmount && $scope.result.maxAmount && parseFloat($scope.result.minAmount) > parseFloat($scope.result.maxAmount) ||
                    $scope.result.minAmount && $scope.result.globalMaxDailyAmount && parseFloat($scope.result.minAmount) > parseFloat($scope.result.globalMaxDailyAmount) ||
                    $scope.result.maxAmount && $scope.result.globalMaxDailyAmount && parseFloat($scope.result.maxAmount) > parseFloat($scope.result.globalMaxDailyAmount)
                ) {
                    toastr.error($translate.instant('accountingOperation.limitsValidateErrorHint'));
                    return;
                }

                var minDefaultAmounts = $scope.result.defaultAmounts.length === 0 ? -1 : Math.min.apply(Math, $scope.result.defaultAmounts.map(function (item) {
                    return item;
                }));

                var maxDefaultAmounts = $scope.result.defaultAmounts.length === 0 ? -1 : Math.max.apply(Math, $scope.result.defaultAmounts.map(function (item) {
                    return item;
                }));

                if (
                    $scope.result.minAmount && minDefaultAmounts !== -1 && parseFloat($scope.result.minAmount) > minDefaultAmounts ||
                    $scope.result.maxAmount && maxDefaultAmounts !== -1 && parseFloat($scope.result.maxAmount) < maxDefaultAmounts ||
                    $scope.result.globalMaxDailyAmount && maxDefaultAmounts !== -1 && parseFloat($scope.result.globalMaxDailyAmount) < maxDefaultAmounts
                ) {
                    toastr.error($translate.instant('accountingOperation.limitsValidateDefaultAmountConfilictErrorHint'));
                    return;
                }

                if($scope.result.wageType === 0){
                    $scope.result.wageRate = $scope.result.wageAmount = null;
                }

                NProgress.start();
                $.blockUI();

                httpServices.editOperationType($scope.result)
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

            //#region DefaultAmounts
            $scope.showDefaultAmountError = false;
            $scope.defaultAmount = '';

            $scope.addDefaultAmount = function () {
                $scope.showDefaultAmountError = true;
                if ($scope.defaultAmount) {
                    var index = $scope.result.defaultAmounts.indexOf(parseFloat($scope.defaultAmount));
                    if (index >= 0) {
                        toastr.error($translate.instant('common.duplicateElement2', {'element': $translate.instant('common.value')}));
                        return;
                    }
                    if ((($scope.result.maxAmount && $scope.defaultAmount > $scope.result.maxAmount) || ($scope.result.globalMaxDailyAmount && $scope.defaultAmount > $scope.result.maxAmount)) ||
                        ($scope.result.maxAmount && $scope.defaultAmount < $scope.result.minAmount)) {
                        toastr.error($translate.instant('accountingOperation.defaultAmountValidateHint'));
                        return;
                    }
                    $scope.result.defaultAmounts.push(parseFloat($scope.defaultAmount));
                    $scope.showDefaultAmountError = false;
                    $scope.validateIp = true;
                    $scope.defaultAmount = '';
                }
            }
            $scope.removeDefaultAmount = function (value) {
                var index = $scope.result.defaultAmounts.indexOf(value);
                $scope.result.defaultAmounts.splice(index, 1);
            }
            //#endregion

            $scope.closeModal = function () {
                $modalInstance.dismiss('cancel');
            }

        }]);
