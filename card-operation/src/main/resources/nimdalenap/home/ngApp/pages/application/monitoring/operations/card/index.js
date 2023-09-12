angular.module('emixApp.controllers').controller('cardoperations_index',
    ['$scope', 'httpServices', '$filter', '$modal', 'shareFunc', 'sharedInfo', '$rootScope', '$translate',
        function ($scope, httpServices, $filter, $modal, shareFunc, sharedInfo, $rootScope, $translate) {
            'use strict';

            //#region user dropbox
            $scope.userEvents = {
                onSelected : function (item){
                    $scope.selectedUser = item;
                    $scope.bindDataGrid();
                }
            }
            $scope.userOptions = {
                id: 'dbxUserCardOperations',
                dataKeyField: 'id',
            };
            $scope.selectedUserId = '';
            $scope.selectedUser = '';
            //#endregion

            //#region report detial
            $scope.openDetails = function (data) {
                var passObject = {};
                passObject.rgOptions = {
                    label: 'accountingOperation.cardOperations',
                    apiUrl: Emix.Api.Application.card.getBankCardOperationWrapperInfo,
                    apiParams: {
                        bankCardOperationId: data.id
                    }
                };

                passObject.rgTemplate = $('#cardOperationStatementDeails').html();

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
                id: 'dtBankCardOperations',
                saveState: false,
                allowFilter: true,
                additionalSortFieldName: 'id',
                apiUrl: Emix.Api.Application.card.getBankCardOperationWrappers,
                apiParams:{
                },
            }
            $scope.dtCols = [
                {
                    name: 'id',
                    title: 'common.id',
                    allowSorting: true,
                    cellClass: 'text-center',
                    headerClass: 'text-center tdmini',
                    dataType: sharedInfo.dataType.int
                },
                {
                    name: 'sourceCard',
                    title: 'card.sourceCard',
                    allowSorting: true,
                    cellClass: 'text-center ltrdir',
                    filterControlClass: 'ltr-input',
                    headerClass: 'text-center col-md-2',
                    dataType: sharedInfo.dataType.string,
                    formatValue: function (value){
                        return (value || '').replace(/(.{4})/g, "$1-").replace(/-$/g, '');
                    },
                    unFormatFilterText: function (value){
                        return value.replaceAll('-', '');
                    }
                },
                {
                    name: 'targetCard',
                    title: 'card.targetCard',
                    allowSorting: true,
                    cellClass: 'text-center ltrdir',
                    filterControlClass: 'ltr-input',
                    headerClass: 'text-center col-md-2',
                    dataType: sharedInfo.dataType.string,
                    formatValue: function (value){
                        return (value || '').replace(/(.{4})/g, "$1-").replace(/-$/g, '');
                    },
                    unFormatFilterText: function (value){
                        return value.replaceAll('-', '');
                    }
                },
                {
                    name: 'targetCardOwner',
                    title: 'card.targetCardOwner',
                    allowSorting: true,
                    cellClass: 'text-center',
                    filterControlClass: 'ltr-input',
                    headerClass: 'text-center col-md-1',
                    dataType: sharedInfo.dataType.string
                },
                {
                    name: 'amount',
                    title: 'common.amount',
                    allowSorting: true,
                    cellClass: 'text-center',
                    headerClass: 'text-center col-md-1',
                    dataType: sharedInfo.dataType.money,
                    cellTemplate: '<span>{{data.amount | currency:"":additionalData.shopFractionSize}} <small>{{additionalData.shopCurrency}}</small></span>'
                },
                {
                    name: 'trackingCode',
                    title: 'common.trackingCode',
                    allowSorting: true,
                    isGlobalFilterParam: true,
                    cellClass: 'text-center',
                    headerClass: 'text-center col-md-2',
                    dataType: sharedInfo.dataType.string
                },
                {
                    name: 'lastStatusDate',
                    keyName: 'createDate',
                    title: 'date',
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
                    headerClass: 'text-center col-md-1',
                    dataType: sharedInfo.dataType.list,
                    filterList: Emix.Api.Application.card.getCardOperatorStatuses,
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
