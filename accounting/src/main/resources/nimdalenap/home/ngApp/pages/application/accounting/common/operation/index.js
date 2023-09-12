angular.module('emixApp.controllers').controller('operationtypelist_index',
    ['$scope', 'httpServices', 'PagerService', '$rootScope', 'shareFunc', 'sharedInfo', '$translate',
        function ($scope, httpServices, PagerService, $rootScope, shareFunc, sharedInfo, $translate) {
            'use strict';

            function getAllOperationTypeWrappers() {

                NProgress.start();
                $.blockUI();

                httpServices.getAllOperationTypeWrappers()
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
            getAllOperationTypeWrappers();

            $scope.editOperationType = function (operationType) {
                var passObject = {operationTypeId: operationType.id};
                shareFunc.openModal(passObject, '/nimdalenap/home/ngApp/pages/application/accounting/common/operation/edit.html', 'editOperationtype_index', null,
                    function (result) {
                        if (sharedInfo.getNeedRefresh())
                            getAllOperationTypeWrappers();
                    }, function () {
                        //dismiss Dialog
                        if (sharedInfo.getNeedRefresh())
                            getAllOperationTypeWrappers();
                    });
            }

            //#region OperationType Table
            $scope.dtOptions = {
                id: 'dtOperationTypes',
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
                    name: 'code',
                    title: 'common.code',
                    allowSorting: true,
                    cellClass: 'text-center',
                    headerClass: 'text-center'
                },
                {
                    name: 'name',
                    title: 'common.name',
                    allowSorting: true,
                    cellClass: 'text-center',
                    headerClass: 'text-center col-md-10'
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
                    headerClass: 'text-center col-md-3',
                    cellClass: 'tdcommand',
                    cellTemplate: '<button class="btn btn-warning" ng-click="events.editOperationType(data);"\n' +
                        '               button-text="common.edit">\n' +
                        '          </button>'
                }
            ];
            $scope.dtData = [];
            $scope.dtAdditionalData = {
                shopCurrency: $rootScope.shopCurrency
            }
            $scope.dtEvents = {
                editOperationType: function (operationType) {
                    $scope.editOperationType(operationType);
                }
            }
            //#endregion

        }]);
