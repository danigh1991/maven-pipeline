angular.module('emixApp.controllers').controller('addaccountcredit_index',
    ['$scope', 'httpServices', 'passObject', '$rootScope', '$filter', 'shareFunc', 'sharedInfo', '$modalInstance', '$translate', '$timeout', '$location',
        function ($scope, httpServices, passObject, $rootScope, $filter, shareFunc, sharedInfo, $modalInstance, $translate, $timeout, $location) {
            'use strict';

            $scope.showBreadcrump = !passObject;

            //#region Properties
            /*$scope.userInputOption = passObject.userInputOption || {};
            $scope.userInputOption.showError = $scope.showError = false;
            $scope.userInputOption.required = true;*/
            //#region user dropbox
            $scope.dbxUserOptions = {
                id: 'dbxAddEditUser',
                dataKeyField: 'id',
                dataCodeField: 'username',
                dataTextField: 'firstName, lastName',
                label: 'common.user',
                additionalSortFieldName: 'id',
                apiUrl: Emix.Api.Users.getUsers,
                required: true,
                disabled: false,
                apiParams: {}
            }
            $scope.dbxUserCols = sharedInfo.getDbxUserCols();
            $scope.dbxUserEvents = {
                onSelected : function (item){
                    $scope.selectedUserName = item !== null ? item.username : null;
                    $scope.selectedUserId = item !== null ? item.id : null;
                    $scope.selectedUser = item;
                    $scope.getAccountPolicyProfiles();
                }
            }
            $scope.selectedUserName = passObject.selectedUserName && passObject.selectedUserName;
            $scope.selectedUserId = passObject.selectedUser && passObject.selectedUser.id;
            $scope.selectedUser = passObject.selectedUser && passObject.selectedUser;
            //#endregion
            //#endregion

            //#region utils

            //#endregion

            $scope.getAccountPolicyProfiles = function () {
                NProgress.start();
                $.blockUI();
                httpServices.getAccountPolicyProfiles(passObject.accountTypeId, $scope.selectedUserId)
                    .then(function (response) {
                        $scope.accountPolicyProfileList = response.data.returnObject;

                        if($scope.editMode)
                            $scope.getAccountWrapperInfo();
                        else if($scope.accountPolicyProfileList && $scope.accountPolicyProfileList.length > 0){
                            $scope.result.accountPolicyProfileId = $scope.accountPolicyProfileList[0].id;
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

            $scope.getAccountWrapperInfo = function () {
                httpServices.getAccountWrapperInfo(passObject.id)
                    .then(function (response) {
                        var data = response.data;
                        $scope.result = {
                            id: data.returnObject.id,
                            name: data.returnObject.name,
                            accountTypeId: data.returnObject.accountTypeId,
                            accountPolicyProfileId: data.returnObject.accountPolicyProfileId,
                            description: data.returnObject.description,
                            color: data.returnObject.color
                        }
                        $scope.selectedUserId = data.returnObject.userId;
                        $scope.dbxUserOptions.disabled = true;
                        /*$scope.userInputOption.userId = data.returnObject.userId;
                        $scope.userInputOption.disabled = true;
                        $scope.userInputOption.events && $scope.userInputOption.events.loadData();*/
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
                    name: '',
                    accountTypeId: passObject.accountTypeId,
                    accountPolicyProfileId: null,
                    description: null,
                    color: '#000000'
                }
            }
            if(!$scope.selectedUser)
                $scope.getAccountPolicyProfiles();

            $scope.ok = function () {
                //$scope.userInputOption.form = $scope.myForm;
                if($scope.editMode) {
                    $scope.edit();
                }
                else {
                    $scope.create();
                }
            }

            $scope.create = function () {

                /*$scope.userInputOption.showError = $scope.showError = true;*/
                $scope.showError = true;

                if (!$scope.myForm.$valid) {
                    toastr.error($translate.instant('common.completeFormCarefully'));
                    return;
                }

                if(!$scope.selectedUserId){
                    toastr.error($translate.instant('common.completeFormCarefully'));
                }else{
                    $scope.result.userId = $scope.selectedUserId;
                }

                NProgress.start();
                $.blockUI();

                httpServices.createAccount($scope.result)
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

                //$scope.userInputOption.showError = $scope.showError = true;
                $scope.showError = true;

                if (!$scope.myForm.$valid) return;

                NProgress.start();
                $.blockUI();

                httpServices.editAccount($scope.result)
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
