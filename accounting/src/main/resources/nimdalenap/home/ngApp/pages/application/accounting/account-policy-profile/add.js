angular.module('emixApp.controllers').controller('addaccountpolicyprofile_index',
    ['$scope', 'httpServices', 'passObject', '$rootScope', '$filter', 'shareFunc', 'sharedInfo', '$modalInstance', '$translate', '$timeout',
        function ($scope, httpServices, passObject, $rootScope, $filter, shareFunc, sharedInfo, $modalInstance, $translate, $timeout) {
            'use strict';

            $scope.showBreadcrump = !passObject;
            //#region user dropbox
            /*$scope.userInputOption = passObject.userInputOption || {};
            $scope.userInputOption.showError = $scope.showError = false;
            $scope.userInputOption.required = false;*/
            $scope.dbxUserOptions = {
                id: 'dbxAddEditUser',
                dataKeyField: 'id',
                dataCodeField: 'username',
                dataTextField: 'firstName, lastName',
                label: 'common.user',
                additionalSortFieldName: 'id',
                apiUrl: Emix.Api.Users.getUsers,
                required: false,
                disabled: false,
                apiParams: {}
            }
            $scope.dbxUserCols = sharedInfo.getDbxUserCols();
            $scope.dbxUserEvents = {
                onSelected : function (item){
                    $scope.selectedUserId = item !== null ? item.id : null;
                    $scope.selectedUser = item;
                }
            }
            $scope.selectedUserId = passObject.selectedUserId && passObject.selectedUserId;
            $scope.selectedUserName = passObject.selectedUserName && passObject.selectedUserName;
            $scope.selectedUser = passObject.selectedUser && passObject.selectedUser;
            //#endregion
            $scope.accountType = passObject.accountType;

            //#region utils
            $scope.operationTypeList = []
            $scope.getSelectedOperationWrappers = function (){
                var selectedOperationWrappers = [];
                for(var i=0; i < $scope.operationTypeList.length; i++){
                    if($scope.operationTypeList[i].selected) {
                        console.log($scope.operationTypeList[i]);
                        selectedOperationWrappers.push({
                            id: $scope.operationTypeList[i].resultOperationTypeWrapper.id,
                            operationTypeId: $scope.operationTypeList[i].operationTypeWrapper.id,
                            minAmount: $scope.operationTypeList[i].resultOperationTypeWrapper.minAmount,
                            maxAmount: $scope.operationTypeList[i].resultOperationTypeWrapper.maxAmount,
                            globalMaxDailyAmount: $scope.operationTypeList[i].resultOperationTypeWrapper.globalMaxDailyAmount,
                            order: $scope.operationTypeList[i].resultOperationTypeWrapper.order,
                            defaultAmounts: $scope.operationTypeList[i].resultOperationTypeWrapper.defaultAmountList || []
                        });
                    }
                }
                return selectedOperationWrappers;
            }
            $scope.fillOperationWrappers = function (){
                for(var i=0; i < $scope.operationTypeWrappers.length; i++){
                    var selected = false;
                    var resultOperationTypeWrapper = null;
                    for(var j=0; j < $scope.result.accountPolicyProfileOperationTypesDto.length; j++){
                        if($scope.operationTypeWrappers[i].id === $scope.result.accountPolicyProfileOperationTypesDto[j].operationTypeId){
                            selected = true;
                            resultOperationTypeWrapper = angular.copy($scope.result.accountPolicyProfileOperationTypesDto[j]);
                        }
                        if(selected)
                            break;
                    }

                    $scope.operationTypeList.push({
                        selected: selected,
                        showDefaultAmountError: false,
                        defaultAmount: '',
                        operationTypeWrapper: angular.copy($scope.operationTypeWrappers[i]),
                        resultOperationTypeWrapper: resultOperationTypeWrapper || {
                            operationTypeId: $scope.operationTypeWrappers[i].id,
                            minAmount: null,
                            maxAmount: null,
                            globalMaxDailyAmount: null,
                            order: null,
                            defaultAmountList: [],
                        }
                    });
                }
            }

            //#region DefaultAmounts
            $scope.addDefaultAmount = function (operationType, txtDefaultValueId) {
                operationType.showDefaultAmountError = true;
                if (operationType.defaultAmount) {
                    var index = (operationType.resultOperationTypeWrapper.defaultAmountList || []).indexOf(parseFloat(operationType.defaultAmount));
                    if (index >= 0) {
                        toastr.error($translate.instant('common.duplicateElement2', {'element': $translate.instant('common.value')}));
                        return;
                    }
                    if (((operationType.resultOperationTypeWrapper.maxAmount && parseFloat(operationType.defaultAmount) > parseFloat(operationType.resultOperationTypeWrapper.maxAmount))
                            || (operationType.resultOperationTypeWrapper.globalMaxDailyAmount && parseFloat(operationType.defaultAmount) > parseFloat(operationType.resultOperationTypeWrapper.globalMaxDailyAmount))) ||
                        (operationType.resultOperationTypeWrapper.minAmount && parseFloat(operationType.defaultAmount) < (operationType.resultOperationTypeWrapper.minAmount))) {
                        toastr.error($translate.instant('accountingOperation.defaultAmountValidateHint'));
                        return;
                    }
                    operationType.resultOperationTypeWrapper.defaultAmountList = (operationType.resultOperationTypeWrapper.defaultAmountList || []);
                    operationType.resultOperationTypeWrapper.defaultAmountList.push(parseFloat(operationType.defaultAmount));
                    operationType.showDefaultAmountError = false;
                    operationType.defaultAmount = '';

                    $timeout(function () {
                        $('#' + txtDefaultValueId).focus();
                    }, 500);
                }
            }

            $scope.removeDefaultAmount = function (operationType, value) {
                var index = operationType.resultOperationTypeWrapper.defaultAmountList.indexOf(value);
                operationType.resultOperationTypeWrapper.defaultAmountList.splice(index, 1);
            }
            //#endregion
            //#endregion

            $scope.getAllAccountOperationTypeWrappers = function () {
                NProgress.start();
                $.blockUI();
                httpServices.getAllAccountOperationTypeWrappers()
                    .then(function (response) {
                        var data = response.data;
                        $scope.operationTypeWrappers = data.returnObject;
                        if($scope.editMode)
                            $scope.getAccountPolicyProfileInfo();
                        else
                            $scope.fillOperationWrappers();
                    }, function (response) {
                        if($scope.editMode){
                            NProgress.done();
                            $.unblockUI();
                        }
                        httpServices.handleError(response.data, response.status, response.headers, response.config);
                    })
                    .finally(function () {
                        if(!$scope.editMode) {
                            NProgress.done();
                            $.unblockUI();
                        }
                    });
            }

            $scope.getAccountPolicyProfileInfo = function () {
                httpServices.getAccountPolicyProfileInfo(passObject.id)
                    .then(function (response) {
                        var data = response.data;
                        $scope.result = {
                            id: data.returnObject.id,
                            name: data.returnObject.name,
                            description: data.returnObject.description,
                            active: data.returnObject.active,
                            accountTypeId: data.returnObject.accountType.id,
                            accountPolicyProfileOperationTypesDto: data.returnObject.accountPolicyProfileOperationTypes
                        }
                        /*$scope.userInputOption.userId = data.returnObject.userId;
                        $scope.userInputOption.disabled = true;
                        $scope.userInputOption.events && $scope.userInputOption.events.loadData();*/
                        $scope.selectedUserId = data.returnObject.userId;
                        $scope.dbxUserOptions.disabled = true;

                        $scope.fillOperationWrappers();
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
                    userId: null,
                    name: '',
                    description: '',
                    active: true,
                    accountTypeId: $scope.accountType.id,
                    accountPolicyProfileOperationTypesDto: []
                }
            }
            $scope.getAllAccountOperationTypeWrappers();

            $scope.ok = function () {

                //$scope.userInputOption.form = $scope.myForm;
                $scope.result.accountPolicyProfileOperationTypesDto = $scope.getSelectedOperationWrappers();

                if($scope.editMode) {
                    $scope.edit();
                }
                else {
                    $scope.create();
                }
            }

            $scope.create = function () {

                //$scope.userInputOption.showError = $scope.showError = true;
                $scope.showError = true;

                if (!$scope.myForm.$valid) {
                    toastr.error($translate.instant('common.completeFormCarefully'));
                    return;
                }

                if($scope.selectedUserId){
                    $scope.result.userId = $scope.selectedUserId;
                }

                NProgress.start();
                $.blockUI();

                httpServices.createAccountPolicyProfile($scope.result)
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

                /*$scope.userInputOption.showError = $scope.showError = true;*/
                $scope.showError = true;

                if (!$scope.myForm.$valid) return;

                NProgress.start();
                $.blockUI();

                httpServices.editAccountPolicyProfile($scope.result)
                    .then(function (response) {
                        var data = response.data;
                        toastr.success($translate.instant('common.successfullySavedMessage'));
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
