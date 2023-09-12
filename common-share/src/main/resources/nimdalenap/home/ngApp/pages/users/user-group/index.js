angular.module('emixApp.controllers').controller('usergrouplist_index',
    ['$scope', 'httpServices', 'PagerService', '$rootScope', 'shareFunc', 'sharedInfo', '$translate', '$routeParams',
        function ($scope, httpServices, PagerService, $rootScope, shareFunc, sharedInfo, $translate, $routeParams) {
            'use strict';

            //#region props
            $scope.isAdminPanel = httpServices.getPanelType() === 'admin';
            //#endregion

            $scope.getAllUserGroupsByUserId = function () {

                if($scope.isAdminPanel && (!$scope.userId || $scope.userId.length === 0)){
                    //toastr.error($translate.instant('validate.requiredMessage', {'element': $translate.instant('common.userId')}));
                    $scope.dtData = [];
                    return;
                }

                NProgress.start();
                $.blockUI();

                httpServices.getAllUserGroupsByUserId($scope.userId)
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
            if(!$scope.isAdminPanel)
                $scope.getAllUserGroupsByUserId();

            $scope.deleteUserGroup = function (userGroup) {

                NProgress.start();
                $.blockUI();

                httpServices.deleteUserGroup(userGroup.id)
                    .then(function (response) {
                        toastr.success($translate.instant('common.successfullyDeletedMessage'));
                        $scope.getAllUserGroupsByUserId();
                    }, function (response) {
                        httpServices.handleError(response.data, response.status, response.headers, response.config);
                    })
                    .finally(function () {
                        NProgress.done();
                        $.unblockUI();
                    });
            }

            $scope.addEdit = function (userGroup) {
                var passObject = {};
                if(userGroup){
                    passObject.id = userGroup.id;
                }else {
                    passObject.userId = $scope.userId;
                    passObject.userName = $scope.selectedUser.username;
                }
                shareFunc.openModal(passObject, '/nimdalenap/home/ngApp/pages/users/user-group/add.html', 'addusergroup_index', 'x-lg',
                    function (result) {
                        $scope.getAllUserGroupsByUserId();
                    }, function () {
                        //dismiss Dialog
                        if(sharedInfo.getNeedRefresh())
                            $scope.getAllUserGroupsByUserId();
                    });
            }

            //#region user dropbox
            $scope.dbxUserOptions = {
                id: 'dbxCustomerClubUser',
                dataKeyField: 'id',
                dataCodeField: 'username',
                dataTextField: 'firstName, lastName',
                label: 'common.user',
                additionalSortFieldName: 'id',
                apiUrl: $scope.isAdminPanel ? Emix.Api.Users.getUsers : '',
                apiParams: {}
            }
            $scope.dbxUserCols = sharedInfo.getDbxUserCols();
            $scope.dbxUserEvents = {
                onSelected : function (item){
                    $scope.selectedUser = item;
                    if(item)
                        $scope.userId = item.id;
                    $scope.getAllUserGroupsByUserId();
                }
            }
            $scope.userId = '';
            $scope.selectedUser = '';
            //#endregion

            //#region OperationType Table
            $scope.dtOptions = {
                id: 'dtUserGroups',
                saveState: true,
                allowFilter: true,
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
                    name: 'statusDesc',
                    title: 'common.status',
                    allowSorting: true,
                    cellClass: 'text-center',
                    headerClass: 'text-center col-md-2',
                    cellTemplate: '<span class="label label-{{data.active ? \'success\' : \'danger\'}}">{{data.active ? (\'common.active\' | translate) : (\'common.inactive\' | translate)}}</span>'
                },

                {
                    name: '',
                    title: '',
                    allowSorting: false,
                    headerClass: 'text-center tdmini',
                    cellClass: 'tdcommand',
                    cellTemplate:
                        '    <button class="btn btn-warning" ng-click="events.edit(data);"\n' +
                        '       button-type="edit">\n' +
                        '    </button>\n' +
                        '    <button class="btn btn-danger" ng-click="events.delete(data);"\n' +
                        '       button-type="delete">\n' +
                        '    </button>\n'
                }
            ];
            $scope.dtData = [];
            $scope.dtAdditionalData = {
            }
            $scope.dtEvents = {
                edit: function (accountCredit) {
                    $scope.addEdit(accountCredit);
                },
                delete: function (data) {
                    $scope.deleteUserGroup(data);
                }
            }
            //#endregion

        }]);
