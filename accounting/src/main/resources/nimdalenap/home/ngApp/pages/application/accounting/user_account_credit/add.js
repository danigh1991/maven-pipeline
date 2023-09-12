angular.module('emixApp.controllers').controller('addaccountcreditdetail_index',
    ['$scope', 'httpServices', 'passObject', '$rootScope', '$filter', 'shareFunc', 'sharedInfo', '$modalInstance', '$translate', '$timeout', '$location',
        function ($scope, httpServices, passObject, $rootScope, $filter, shareFunc, sharedInfo, $modalInstance, $translate, $timeout, $location) {
            'use strict';

            $scope.showError = false;
            $scope.showBreadcrump = !passObject;
            $scope.creditTypeList = sharedInfo.getCreditTypeList();
            $scope.creditViewTypeList = sharedInfo.getCreditViewTypeList();

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

            $scope.getAccountPolicyProfiles = function () {
                NProgress.start();
                $.blockUI();
                httpServices.getAccountPolicyProfiles(passObject.accountTypeId)
                    .then(function (response) {
                        $scope.accountPolicyProfileList = response.data.returnObject;

                        if($scope.editMode)
                            $scope.getAccountCreditDetailInfo();
                        else if($scope.accountPolicyProfileList && $scope.accountPolicyProfileList.length > 0){
                            $scope.result.defaultAccountPolicyProfileId = $scope.accountPolicyProfileList[0].id;
                        }

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

            $scope.getAccountCreditDetailInfo = function () {

                httpServices.getAccountCreditDetailInfo(passObject.id)
                    .then(function (response) {
                        var data = response.data;
                        $scope.resultForDisplay = {
                            remainCreditAmount: data.returnObject.remainCreditAmount,
                            accountName: data.returnObject.account.name,
                            accountBalance: data.returnObject.account.balance,
                            accountBlocked: data.returnObject.account.block,
                        }
                        $scope.result = {
                            id: data.returnObject.id,
                            accountId: passObject.accountId,
                            title: data.returnObject.title,
                            creditTypeId: data.returnObject.creditType,
                            viewType: data.returnObject.viewType,
                            expireDate: data.returnObject.expireDate,
                            defaultAccountPolicyProfileId: data.returnObject.defaultAccountPolicyProfile.id,
                            creditAmount: data.returnObject.creditAmount,
                            creditAmountPerUser: data.returnObject.creditAmountPerUser,
                            minCreditAmount: data.returnObject.minCreditAmount,
                            spendingRestrictions: data.returnObject.spendingRestrictions,
                            rateRestrictions: data.returnObject.rateRestrictions,
                            maxAmountRestrictions: data.returnObject.maxAmountRestrictions,
                            //interestRate: data.returnObject.interestRate,
                            //forfeitRate: data.returnObject.forfeitRate,
                            issuer: data.returnObject.issuer,
                            description: data.returnObject.description,
                            active: data.returnObject.active,
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
                    title: '',
                    creditTypeId: $scope.creditTypeList[0].id,
                    viewType: $scope.creditViewTypeList[0].id,
                    expireDate: '',
                    defaultAccountPolicyProfileId: null,
                    creditAmount: '',
                    creditAmountPerUser: '',
                    minCreditAmount: 0,
                    spendingRestrictions: false,
                    rateRestrictions: '',
                    maxAmountRestrictions: '',
                    //interestRate: 0,
                    //forfeitRate: 0,
                    issuer:'',
                    description: '',
                    active: false
                }
            }

            $scope.getAccountPolicyProfiles();

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

                httpServices.createAccountCreditDetail($scope.result)
                    .then(function (response) {
                        var data = response.data;
                        sharedInfo.setNeedRefresh(true);
                        setTimeout($scope.closeModal(),1000);
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

                httpServices.editAccountCreditDetail($scope.result)
                    .then(function (response) {
                        var data = response.data;
                        toastr.success($translate.instant('common.successfullySavedMessage'));
                        $scope.getAccountCreditDetailInfo();
                        sharedInfo.setNeedRefresh(true);
                        //setTimeout($scope.closeModal(),1000);
                    }, function (response) {
                        httpServices.handleError(response.data, response.status, response.headers, response.config);
                    })
                    .finally(function () {
                        NProgress.done();
                        $.unblockUI();
                    });
            }

            $scope.closeModal = function(){
                $modalInstance.dismiss('cancel');
            }

        }]);
