angular.module('emixApp.controllers').controller('accountoperations_index',
    ['$scope', 'httpServices', '$filter', '$modal', 'shareFunc', 'sharedInfo', '$rootScope','$translate', '$route',
        function ($scope, httpServices, $filter, $modal, shareFunc, sharedInfo, $rootScope, $translate, $route) {
            'use strict';

            //#region user dropbox
            $scope.userEvents = {
                onSelected : function (item){
                    $scope.selectedUser = item;
                    $scope.bindDataGrid();
                }
            }
            $scope.userOptions = {
                id: 'dbxUserAccountOperations',
                dataKeyField: 'id',
            };
            $scope.selectedUserId = '';
            $scope.selectedUser = '';
            //#endregion

            //#region report detial
            $scope.openDetails = function (data) {
                var passObject = {};
                passObject.rgOptions = {
                    label: 'accountingOperation.accountingOperations',
                    apiUrl: Emix.Api.Application.operation.getOperationRequestWrapperInfo,
                    apiParams: {
                        operationRequestId: data.id
                    }
                };

                passObject.rgTemplate = $('#operationStatementDeails').html();

                passObject.rgRows = [];
                passObject.additionalData = {
                    shopCurrency: $rootScope.shopCurrency,
                    shopFractionSize: $rootScope.shopFractionSize,
                };
                passObject.events = {};
                passObject.allowPrint = true;
                shareFunc.openModal(passObject, '/panelshare/directives/report-viewer/modal/index.html', 'reportviewermodal_index', 'lg',
                    function (result) {
                    }, function () {
                    });
            }
            //#endregion

            //#region Table
            $scope.bindDataGrid = function (){
                $scope.dtOptions.apiParams.userId = $scope.selectedUserId;
                $scope.dtEvents.dataBind();
            }
            $scope.dtOptions = {
                id: 'dtAccountOperations',
                saveState: true,
                allowFilter: true,
                additionalSortFieldName: 'id',
                apiUrl: Emix.Api.Application.operation.getOperationRequestWrappers,
                apiParams:{
                },
            }
            $scope.dtCols = [
                {
                    name: 'id',
                    title: 'common.id',
                    allowSorting: true,
                    cellClass: 'text-center',
                    headerClass: 'text-center col-md-1',
                    dataType: sharedInfo.dataType.int
                },
                {
                    name: 'title',
                    keyName: 'operationTypeId',
                    title: 'accounting.operationType',
                    allowSorting: true,
                    cellClass: 'text-center',
                    headerClass: 'text-center col-md-3',
                    dataType: sharedInfo.dataType.list,
                    filterList: Emix.Api.Application.OperationType.getAllOperationTypeWrappers,
                    filterListKey: 'id',
                    filterListValue: 'caption',
                },
                {
                    name: 'amount',
                    title: 'common.amount',
                    allowSorting: true,
                    cellClass: 'text-center',
                    headerClass: 'text-center col-md-2',
                    dataType: sharedInfo.dataType.money,
                    cellTemplate: '<span>{{data.amount | currency:"":additionalData.shopFractionSize}} <small>{{additionalData.shopCurrency}}</small></span>'
                },
                {
                    name: 'trackingCode1',
                    keyName: 'trackingCode',
                    title: 'common.trackingCode',
                    allowSorting: true,
                    isGlobalFilterParam: true,
                    cellClass: 'text-center',
                    headerClass: 'text-center col-md-2',
                    dataType: sharedInfo.dataType.string
                },
                {
                    name: 'createDate',
                    title: 'common.createDate',
                    allowSorting: true,
                    cellClass: 'text-center',
                    headerClass: 'text-center col-md-2',
                    dataType: sharedInfo.dataType.date
                },
                {
                    name: 'statusDesc',
                    keyName: 'status',
                    title: 'common.status',
                    allowSorting: false,
                    cellClass: 'text-center',
                    headerClass: 'text-center col-md-2',
                    dataType: sharedInfo.dataType.list,
                    filterList: Emix.Api.Application.operation.getOperationRequestStatuses,
                    filterListKey: 'id',
                    filterListValue: 'caption',
                },
                {
                    name: '',
                    title: '',
                    allowSorting: false,
                    headerClass: 'text-center',
                    cellClass: 'tdcommand',
                    cellTemplate: '<button class="btn btn-warning" ng-click="events.detail(data);"\n' +
                        '               button-text="accounting.statement">\n' +
                        '          </button>'
                }
            ];
            $scope.dtData = [];
            $scope.dtAdditionalData = {
                shopCurrency: $rootScope.shopCurrency,
                shopFractionSize: $rootScope.shopFractionSize,
            }
            $scope.dtEvents = {
                detail: function (data){
                    $scope.openDetails(data);
                }
            }
            //#endregion
        }]);
