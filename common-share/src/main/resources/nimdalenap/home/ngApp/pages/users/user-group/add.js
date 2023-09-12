angular.module('emixApp.controllers').controller('addusergroup_index',
    ['$scope', 'httpServices', 'passObject', '$rootScope', '$filter', 'shareFunc', 'sharedInfo', '$modalInstance', '$translate', '$timeout', '$location',
        function ($scope, httpServices, passObject, $rootScope, $filter, shareFunc, sharedInfo, $modalInstance, $translate, $timeout, $location) {
            'use strict';

            //#region props
            $scope.isAdminPanel = httpServices.getPanelType() === 'admin';
            $scope.showError = false;
            $scope.showBreadcrump = !passObject;
            //#endregion

            //#region utils

            $scope.deleteGroupMemberByUserName = function (data) { 
                NProgress.start();
                $.blockUI();
                httpServices.deleteGroupMemberByUserName(passObject.id, data)
                    .then(function (response) {
                        toastr.success($translate.instant('common.successfullyDeletedMessage'));
                        $scope.getUserNamesByGroupId();
                    }, function (response) {
                        httpServices.handleError(response.data, response.status, response.headers, response.config);
                        NProgress.done();
                        $.unblockUI();
                    })
                    .finally(function () {
                    });
            }
            $scope.addGroupMemberByUserName = function () {
                if (!($scope.groupUserId && $scope.groupUserId.toString().length > 0)) {
                    toastr.error($translate.instant('validate.requiredMessage', {'element': $translate.instant($scope.isAdminPanel ? 'common.userName' : 'common.customer')}));
                    return;
                }

                NProgress.start();
                $.blockUI();

                httpServices.addGroupMemberByUserId(passObject.id, $scope.groupUserId)
                    .then(function (response) {
                        toastr.success($translate.instant('common.successfullySavedMessage'));
                        //$scope.groupUserId = 0;
                        $scope.dbxUserEvents.erase();
                        $scope.getUserNamesByGroupId();
                    }, function (response) {
                        httpServices.handleError(response.data, response.status, response.headers, response.config);
                        NProgress.done();
                        $.unblockUI();
                    })
                    .finally(function () {

                    });
            }
            //#endregion

            //#region user dropbox
            var getDbxOptions = function () {

                if ($scope.isAdminPanel) {
                    return { //all users
                        id: 'dbxUserGroupEditUser',
                        dataKeyField: 'id',
                        dataCodeField: 'username',
                        dataTextField: 'firstName, lastName',
                        label: 'common.user',
                        additionalSortFieldName: 'id',
                        apiUrl: Emix.Api.Users.getUsers,
                        apiParams: {},
                        readOnly: true
                    };
                } else { // only merchant users
                    return {
                        id: 'dbxUserGroupEditUser',
                        dataKeyField: 'customerUserId',
                        dataCodeField: 'maskCustomerUserName',
                        dataTextField: 'fullName',
                        label: 'common.customer',
                        additionalSortFieldName: 'customerUserId',
                        apiUrl: Emix.Api.Application.merchant.getMerchantCustomers,
                        apiParams: {},
                        readOnly: true
                    };
                }

            }
            $scope.dbxUserOptions = getDbxOptions();
            $scope.dbxUserCols = $scope.isAdminPanel ? sharedInfo.getDbxUserCols() : sharedInfo.getDbxMerchantUserCols();
            $scope.dbxUserEvents = {
                onSelected : function (item){
                    $scope.selectedUser = item;
                }
            }
            $scope.groupUserId = 0;
            $scope.selectedUser = '';
            //#endregion

            $scope.getUserNamesByGroupId = function () {

                httpServices.getUserNamesByGroupId(passObject.id)
                    .then(function (response) {
                        var data = response.data;
                        $scope.dtData = data.returnObject;
                    }, function (response) {
                        httpServices.handleError(response.data, response.status, response.headers, response.config);
                    })
                    .finally(function () {
                        NProgress.done();
                        $.unblockUI();
                    });
            }

            $scope.getUserGroupInfo = function () {
                NProgress.start();
                $.blockUI();
                httpServices.getUserGroupInfo(passObject.id)
                    .then(function (response) {
                        var data = response.data;
                        $scope.result = {
                            id: data.returnObject.id,
                            /*storeBranchId: data.returnObject.targetTypeId === 2 ? data.returnObject.storeBranchId : '',
                            userId: data.returnObject.targetTypeId === 31 ? data.returnObject.userId : '',*/
                            name: data.returnObject.name,
                            description: data.returnObject.description,
                            active: data.returnObject.active,
                        }

                        $scope.getUserNamesByGroupId();
                    }, function (response) {
                        httpServices.handleError(response.data, response.status, response.headers, response.config);

                        NProgress.done();
                        $.unblockUI();
                    })
                    .finally(function () {

                    });
            }

            if(passObject && passObject.id){
                $scope.editMode = true;
                $scope.getUserGroupInfo();
            }
            else{
                $scope.result = {
                    id: null,
                    storeBranchId: null,
                    userId: passObject.userId || '',
                    userName: passObject.userName || '',
                    name: '',
                    description: '',
                    active: false
                }
            }

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

                httpServices.createUserGroup($scope.result)
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

                httpServices.editUserGroup($scope.result)
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

            //#region Grid Option
            $scope.dtOptions = {
                id: 'dtUserGroupUsers',
                pageSize: 15,
                saveState: true,
                allowFilter: true,
                additionalSortFieldName: 'id',
                rowClass: 'col-md-2 col-sm-4 col-xs-6 pdr-0 pdl-5',
                //rowNgClass: '\'hsbrnchextrsrvc\' : data.storeBranchId'
            }
            $scope.dtData = [];
            $scope.dtAdditionalData = {}
            $scope.dtEvents = {
                delete: function (data) {
                    $scope.deleteGroupMemberByUserName(data);
                },
            }
            //#endregion

            $scope.closeModal = function(){
                $modalInstance.dismiss('cancel');
            }

        }]);
