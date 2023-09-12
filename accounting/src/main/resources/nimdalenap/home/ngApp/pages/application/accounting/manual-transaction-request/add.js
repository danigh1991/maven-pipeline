angular.module('emixApp.controllers').controller('addmanualtransactionrequest_index',
    ['$scope', 'httpServices', 'passObject', '$rootScope', '$filter', 'shareFunc', 'sharedInfo', '$modalInstance', '$translate', '$timeout', '$location',
        function ($scope, httpServices, passObject, $rootScope, $filter, shareFunc, sharedInfo, $modalInstance, $translate, $timeout, $location) {
            'use strict';

            $scope.showError = false;
            $scope.showBreadcrump = !passObject;

            //#region utils
            $scope.changeStateAccountCreditDetail = function (data) {
                NProgress.start();
                $.blockUI();
                httpServices.changeStateAccountCreditDetail(data.id)
                    .then(function (response) {
                        $scope.result.active = response.data.returnObject;
                        var message = data.active ? $translate.instant('common.successfullyActivedElement', {'element' : ($translate.instant('accounting.accountCreditDetails'))}) : $translate.instant('common.successfullyInactivedElement', {'element' : ($translate.instant('accounting.accountCreditDetails'))});
                        toastr.success(message);
                        sharedInfo.setNeedRefresh(true);
                    }, function (response) {
                        httpServices.handleError(response.data, response.status, response.headers, response.config);
                    })
                    .finally(function () {
                        NProgress.done();
                        $.unblockUI();
                    });
            }
            //#endregion
            $scope.params = {};
            $scope.getAccountInfo = function () {
                NProgress.start();
                $.blockUI();
                httpServices.getAccountInfo(passObject.accountId)
                    .then(function (response) {
                        $scope.accountInfo = response.data.returnObject
                        $scope.params.accounts = [$scope.accountInfo];
                        if($scope.editMode)
                            $scope.getManualTransactionRequestWrapperInfo();

                    }, function (response) {
                        httpServices.handleError(response.data, response.status, response.headers, response.config);
                        if($scope.editMode){
                            NProgress.done();
                            $.unblockUI();
                        }
                    })
                    .finally(function () {
                        if(!$scope.editMode) {
                            NProgress.done();
                            $.unblockUI();
                        }
                    });
            }

            $scope.getManualTransactionRequestWrapperInfo = function () {

                httpServices.getManualTransactionRequestWrapperInfo(passObject.id)
                    .then(function (response) {
                        var data = response.data;
                        $scope.manualTransactionRequest = data.returnObject;

                        $scope.result = {
                            id: data.returnObject.id,
                            accountId: passObject.accountId,
                            reference: data.returnObject.reference,
                            referenceDate: sharedInfo.getValidTime(data.returnObject.referenceDate),
                            description: data.returnObject.description,
                            amount: data.returnObject.amount
                        }
                    }, function (response) {
                        httpServices.handleError(response.data, response.status, response.headers, response.config);
                    })
                    .finally(function () {
                        NProgress.done();
                        $.unblockUI();
                    });
            }

            if(passObject && passObject.id){
                $scope.editMode = true;
            }
            else{
                $scope.result = {
                    id: null,
                    accountId: passObject.accountId,
                    reference: '',
                    referenceDate: '',
                    description: '',
                    amount: ''
                }
            }

            $scope.getAccountInfo();

            $scope.ok = function () {
                if($scope.editMode) {
                    $scope.edit();
                }
                else {
                    $scope.create();
                }
            }

            $scope.create = function () {

                $scope.showError = true;

                if (!$scope.myForm.$valid) {
                    toastr.error($translate.instant('common.completeFormCarefully'));
                    return;
                }

                NProgress.start();
                $.blockUI();

                httpServices.createManualTransactionRequest($scope.result)
                    .then(function (response) {
                        var data = response.data;
                        sharedInfo.setNeedRefresh(true);
                        $scope.editMode = true;
                        passObject.id = data.returnObject;
                        setTimeout(function (){ $scope.getAccountInfo(); },0);
                    }, function (response) {
                        httpServices.handleError(response.data, response.status, response.headers, response.config);
                    })
                    .finally(function () {
                        NProgress.done();
                        $.unblockUI();
                    });
            }

            $scope.edit = function () {

                $scope.showError = true;

                if (!$scope.myForm.$valid) return;

                NProgress.start();
                $.blockUI();

                httpServices.editManualTransactionRequest($scope.result)
                    .then(function (response) {
                        var data = response.data;
                        toastr.success($translate.instant('common.successfullySavedMessage'));
                        $scope.getAccountInfo();
                        sharedInfo.setNeedRefresh(true);
                    }, function (response) {
                        httpServices.handleError(response.data, response.status, response.headers, response.config);
                    })
                    .finally(function () {
                        NProgress.done();
                        $.unblockUI();
                    });
            }

            $scope.closeModal = function() {
                $modalInstance.dismiss('cancel');
            }

            $scope.approve = function (obj){
                //#region Get Confirm Reason Text
                var passObject = {
                    title: $translate.instant('common.verify'),
                    inputType: 'textarea',
                    inputCaption: $translate.instant('common.description'),
                    required: false
                };
                shareFunc.openModal(passObject, '/nimdalenap/home/ngApp/pages/modal/prompt.html', 'promptmodal_index', 'md',
                    function (result) {
                        var approveManualTransactionRequestObj = {
                            id: obj.id,
                            status: 1,
                            approvedDescription: result,
                        }
                        $scope.approveManualTransactionRequest(approveManualTransactionRequestObj);

                    }, function () {
                    });
                //#endregion

            }

            $scope.cancel = function (obj){
                //#region Get Confirm Reason Text
                var passObject = {
                    title: $translate.instant('cancel'),
                    inputType: 'textarea',
                    inputCaption: $translate.instant('common.description'),
                    required: true
                };
                shareFunc.openModal(passObject, '/nimdalenap/home/ngApp/pages/modal/prompt.html', 'promptmodal_index', 'md',
                    function (result) {

                        var approveManualTransactionRequestObj = {
                            id: obj.id,
                            status: 2,
                            approvedDescription: result,
                        }
                        $scope.approveManualTransactionRequest(approveManualTransactionRequestObj);
                    }, function () {
                    });
                //#endregion

            }

            $scope.approveManualTransactionRequest = function (manualTransactionRequestApproveDto) {

                NProgress.start();
                $.blockUI();
                httpServices.approveManualTransactionRequest(manualTransactionRequestApproveDto)
                    .then(function (response) {
                        var data = response.data;
                        toastr.success($translate.instant('common.successfullySavedMessage'));
                        $scope.getAccountInfo();
                        sharedInfo.setNeedRefresh(true);
                    }, function (response) {
                        httpServices.handleError(response.data, response.status, response.headers, response.config);
                    })
                    .finally(function () {
                        NProgress.done();
                        $.unblockUI();
                    });
            }

        }]);
