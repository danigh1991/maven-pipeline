angular.module('emixApp.controllers').controller('mobilechargeandinternet_index',
    ['$scope', 'httpServices', '$filter', '$modal', 'shareFunc', 'sharedInfo', '$translate', '$route',
        function ($scope, httpServices, $filter, $modal, shareFunc, sharedInfo, $translate, $route) {
            'use strict';

            $scope.typeId = $route.current.$$route.originalPath.includes('mobilecharge') ? 1 : 2;//1 ==> mobile charge, 2 ==> internet package

            //#region user dropbox
            $scope.userEvents = {
                onSelected : function (item){
                    $scope.selectedUser = item;
                    $scope.bindDataGrid();
                }
            }
            $scope.userOptions = {
                id: 'dbxUserMobileOperations',
                dataKeyField: 'id',
            };
            $scope.selectedUserId = '';
            $scope.selectedUser = '';
            //#endregion

            //#region Table
            $scope.bindDataGrid = function (){
                //$scope.dtOptions.apiParams.operator = $scope.selectedOperatorName;
                $scope.dtOptions.apiParams.userId = $scope.selectedUserId;
                $scope.dtEvents.dataBind();
            }
            $scope.dtOptions = {
                id: 'dtMobileOperations',
                saveState: true,
                allowFilter: true,
                additionalSortFieldName: 'id',
                apiUrl: Emix.Api.Application.topup.getTopUpRequestWrappers,
                apiParams:{
                    typeId: $scope.typeId
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
                    name: 'phoneNumber',
                    title: 'common.mobileNumber',
                    allowSorting: true,
                    cellClass: 'text-center',
                    headerClass: 'text-center col-md-2',
                    dataType: sharedInfo.dataType.string
                },
                {
                    name: 'operatorName',
                    keyName: 'operator',
                    title: 'common.operator',
                    allowSorting: false,
                    cellClass: 'text-center',
                    headerClass: 'text-center col-md-2',
                    dataType: sharedInfo.dataType.list,
                    filterList: Emix.Api.Application.topup.getOperators,//sharedInfo.getOperatorList(),
                    filterListKey: 'name',
                    filterListValue: 'caption',
                },
                {
                    name: 'trackingCode',
                    title: 'common.trackingCode',
                    allowSorting: true,
                    isGlobalFilterParam: true,
                    cellClass: 'text-center',
                    headerClass: 'text-center col-md-1',
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
                    filterList: Emix.Api.Application.topup.topupgetTopUpRequestStatuses,
                    filterListKey: 'id',
                    filterListValue: 'caption',
                },
                {
                    name: 'description',
                    title: 'common.description',
                    allowSorting: true,
                    cellClass: 'text-center',
                    headerClass: 'text-center col-md-2',
                    dataType: sharedInfo.dataType.string
                },
                {
                    name: '',
                    title: '',
                    allowSorting: false,
                    headerClass: 'text-center',
                    cellClass: 'tdcommand',
                }
            ];
            $scope.dtData = [];
            $scope.dtAdditionalData = {}
            $scope.dtEvents = {}
            //#endregion
        }]);
