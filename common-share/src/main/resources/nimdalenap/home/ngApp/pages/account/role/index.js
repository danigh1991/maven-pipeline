angular.module('emixApp.controllers').controller('rolelist_index',
    ['$scope', 'httpServices', 'PagerService', '$rootScope', 'shareFunc', 'sharedInfo', '$translate',
        function ($scope, httpServices, PagerService, $rootScope, shareFunc, sharedInfo, $translate) {
            'use strict';

            function getAllRoles() {

                NProgress.start();
                $.blockUI();

                httpServices.getAllRoles()
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

            getAllRoles();

            $scope.editRole = function (roleId) {
                var passObject = {roleId: roleId};
                shareFunc.openModal(passObject, '/nimdalenap/home/ngApp/pages/account/role/edit.html', 'editrole_index', null,
                    function (result) {
                        if (sharedInfo.getNeedRefresh())
                            getAllRoles();
                    }, function () {
                        //dismiss Dialog
                        if (sharedInfo.getNeedRefresh())
                            getAllRoles();
                    });
            }


            $scope.deleteRole = function (roleId) {
                NProgress.start();
                $.blockUI();

                httpServices.deleteRole(roleId)
                    .then(function (response) {
                        var data = response.data;
                        toastr.success($translate.instant('common.successfullyDeletedMessage'));
                        getAllRoles();
                    }, function (response) {
                        httpServices.handleError(response.data, response.status, response.headers, response.config);
                    })
                    .finally(function () {
                        NProgress.done();
                        $.unblockUI();
                    });
            }

            //#region Role Table
            $scope.dtOptions = {
                id: 'dtRoles',
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
                    headerClass: 'text-center'
                },
                {
                    name: 'roleName',
                    title: 'common.name',
                    allowSorting: true,
                    cellClass: 'text-center',
                    headerClass: 'text-center col-md-3'
                },
                {
                    name: 'roleTypeDesc',
                    title: 'common.description',
                    allowSorting: true,
                    cellClass: 'text-center',
                    headerClass: 'text-center col-md-3'
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
                    headerClass: 'text-center col-md-2',
                    cellClass: 'tdcommand',
                    cellTemplate: '<button class="btn btn-warning" ng-click="events.editRole(data);"\n' +
                        '               button-text="common.edit">\n' +
                        '          </button>' +
                        '          <button class="btn btn-danger" deletebutton \n' +
                        '               ng-click="events.deleteRole(data);" button-type="delete">' +
                        '          </button>'
                }
            ];
            $scope.dtData = [];
            $scope.dtAdditionalData = {
                shopCurrency: $rootScope.shopCurrency
            }
            $scope.dtEvents = {
                editRole: function (role) {
                    $scope.editRole(role.id);
                },
                deleteRole: function (role) {
                    $scope.deleteRole(role.id);
                }
            }
            //#endregion

        }]);
