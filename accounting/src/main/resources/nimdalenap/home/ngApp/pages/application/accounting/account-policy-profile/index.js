angular.module('emixApp.controllers').controller('accountpolicyprofilelist_index',
    ['$scope', 'httpServices', 'PagerService', '$rootScope', 'shareFunc', 'sharedInfo', '$translate', '$route', '$timeout',
        function ($scope, httpServices, PagerService, $rootScope, shareFunc, sharedInfo, $translate, $route, $timeout) {
            'use strict';

            /*$scope.userInputOption = {
            }*/
            //#region user dropbox
            $scope.selectedUserId;
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
                    $scope.selectedUser = item;
                    $scope.selectedUserName = item !== null ? item.username : null;
                    $scope.search();
                }
            }
            //#endregion
            //#region accountType
            $scope.accountTypeFilter = function (item) {
                var accountType = parseInt($scope.accountType.id, 10);
                return item.accountTypeId === accountType;
            }

            $scope.accountTypeChange = function (){
                 $scope.getAccountPolicyProfiles();
            }

            $scope.getAccountPolicyProfiles = function () {

                NProgress.start();
                $.blockUI();

                httpServices.getAccountPolicyProfiles($scope.accountType.id, $scope.selectedUserId)
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

            $scope.getAllActiveAccountTypes = function () {
                NProgress.start();
                $.blockUI();

                httpServices.getAllActiveAccountTypes()
                    .then(function (response) {
                        var data = response.data;
                        $scope.allActiveAccountTypes = data.returnObject;
                        $scope.accountType = $scope.allActiveAccountTypes[0];
                        $scope.getAccountPolicyProfiles();
                    }, function (response) {
                        httpServices.handleError(response.data, response.status, response.headers, response.config);
                    })
                    .finally(function () {
                        NProgress.done();
                        $.unblockUI();
                    });
            }

            $scope.getAllActiveAccountTypes();
            //#endregion

            //#region Utils
            $scope.afterRestoreSavedState = function (){

            }
            $scope.search = function (){
                $timeout(function () {
                    $scope.getAccountPolicyProfiles();
                }, 300);
            }
            $scope.addEdit = function (accountPolicyCredit) {
                var passObject = {
                    accountType: $scope.accountType
                };

                if(accountPolicyCredit){
                    passObject.id = accountPolicyCredit.id;
                }else{
                    //passObject.userInputOption = angular.copy($scope.userInputOption);
                    passObject.selectedUserId = angular.copy($scope.selectedUserId);
                    passObject.selectedUserName = angular.copy($scope.selectedUserName);
                    passObject.selectedUser = angular.copy($scope.selectedUser);
                }

                shareFunc.openModal(passObject, '/nimdalenap/home/ngApp/pages/application/accounting/account-policy-profile/add.html', 'addaccountpolicyprofile_index', 'x-lg',
                    function (result) {
                        $scope.search();
                    }, function () {
                        //dismiss Dialog
                        if(sharedInfo.getNeedRefresh())
                            $scope.search();
                    });
            }
            $scope.deleteAccountPolicyProfile = function (data) {
                NProgress.start();
                $.blockUI();
                httpServices.deleteAccountPolicyProfile(data.id)
                    .then(function (response) {
                        toastr.success($translate.instant('common.successfullyDeletedMessage'));
                        $scope.search();
                    }, function (response) {
                        httpServices.handleError(response.data, response.status, response.headers, response.config);
                    })
                    .finally(function () {
                        NProgress.done();
                        $.unblockUI();
                    });
            }
            //#endregion

            //#region OperationType Table
            $scope.dtOptions = {
                id: 'dtAccountPolicyProfile',
                saveState: true,
                allowFilter: false,
                additionalSortFieldName: 'id'
            }
            $scope.dtCols = [
                {
                    name: 'id',
                    title: 'common.id',
                    allowSorting: true,
                    cellClass: 'text-center',
                    headerClass: 'text-center tdmini'
                },
                {
                    name: 'name',
                    title: 'common.name',
                    allowSorting: true,
                    cellClass: 'text-center',
                    headerClass: 'text-center col-md-8'
                },
                {
                    name: 'active',
                    title: 'common.status',
                    allowSorting: true,
                    headerClass: 'text-center col-md-1',
                    cellClass: 'text-center',
                    cellTemplate: ' <span class="label label-{{data.active ? \'success\' : \'danger\'}}">{{data.active ? (\'common.active\' | translate) : (\'common.inactive\' | translate)}}</span>'
                },
                {
                    name: '',
                    title: '',
                    allowSorting: false,
                    headerClass: 'text-center tdmini',
                    cellClass: 'tdcommand',
                    cellTemplate:
                        '           <button class="btn btn-warning" ng-click="events.edit(data);"\n' +
                        '              button-type="edit">\n' +
                        '           </button>\n' +
                        '            <button class="btn btn-danger" ng-click="events.delete(data);"\n' +
                        '               button-type="delete">\n' +
                        '            </button>\n'
                }
            ];
            $scope.dtData = [];
            $scope.dtAdditionalData = {
                shopCurrency: $rootScope.shopCurrency,
                shopFractionSize: $rootScope.shopFractionSize,
            }
            $scope.dtEvents = {
                delete: function (data) {
                    $scope.deleteAccountPolicyProfile(data);
                },
                edit: function (data) {
                    $scope.addEdit(data);
                }
            }
            //#endregion

        }]);
