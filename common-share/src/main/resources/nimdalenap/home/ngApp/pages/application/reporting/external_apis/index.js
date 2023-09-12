angular.module('emixApp.controllers').controller('externalapilist_index',
    ['$scope', 'httpServices', 'PagerService', '$rootScope', 'shareFunc', 'sharedInfo', '$translate',
        function ($scope, httpServices, PagerService, $rootScope, shareFunc, sharedInfo, $translate) {
            'use strict';

            //#region external api list Table
            $scope.dtOptions = {
                id: 'dtExternalApiList',
                apiUrl: Emix.Api.Application.externalApi.getExternalApiCallWrappers,
                apiParams: {},
                saveState: false,
                allowFilter: true,
                additionalSortFieldName: 'id'
            }

            $scope.dtCols = [
                {
                    name: 'id',
                    title: 'common.id',
                    allowSorting: true,
                    cellClass: 'text-center',
                    headerClass: 'text-center tdmini',
                    dataType: sharedInfo.dataType.int,
                },
                {
                    name: 'apiDescription',
                    keyName: 'apiId',
                    title: 'common.apiName',
                    allowSorting: true,
                    cellClass: 'text-center',
                    headerClass: 'text-center col-md-2',
                    dataType: sharedInfo.dataType.list,
                    filterList: Emix.Api.Application.externalApi.getAllExternalApis,
                    filterListKey: 'id',
                    filterListValue: 'name',
                },
                {
                    name: 'processId',
                    title: 'common.processId',
                    allowSorting: true,
                    cellClass: 'text-center',
                    headerClass: 'text-center col-md-2',
                },
                {
                    name: 'referenceId',
                    title: 'common.referenceId',
                    allowSorting: true,
                    cellClass: 'text-center',
                    headerClass: 'text-center col-md-1',
                },
                {
                    name: 'requestKey',
                    title: 'common.requestKey',
                    allowSorting: true,
                    cellClass: 'text-center',
                    headerClass: 'text-center col-md-1',
                },
                {
                    name: 'trackingId',
                    title: 'common.trackingCode',
                    allowSorting: true,
                    cellClass: 'text-center',
                    headerClass: 'text-center col-md-2',
                },
                {
                    name: 'statusDesc',
                    keyName: 'status',
                    title: 'common.status',
                    allowSorting: true,
                    cellClass: 'text-center',
                    headerClass: 'text-center col-md-2',
                    dataType: sharedInfo.dataType.list,
                    filterList: Emix.Api.Application.externalApi.getExternalApiCallStatuses,
                    filterListKey: 'id',
                    filterListValue: 'caption',
                },

                {
                    name: 'createDate',
                    title: 'common.createDate',
                    allowSorting: true,
                    cellClass: 'text-center',
                    headerClass: 'text-center col-md-2',
                    dataType: sharedInfo.dataType.date,
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
            $scope.dtAdditionalData = {
                shopCurrency: $rootScope.shopCurrency,
                shopFractionSize: $rootScope.shopFractionSize,
            }
            $scope.dtEvents = {
            }
            //#endregion

        }]);
