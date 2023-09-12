var customerList = function ($scope, httpServices, PagerService, $rootScope, shareFunc, sharedInfo, $modalInstance, passObject) {
    'use strict';

    $scope.showBreadcrump = !passObject;

    $scope.merchantId = $scope.showBreadcrump ? 0 : passObject.merchantId;

    var params = $scope.merchantId > 0 ? {merchantId: $scope.merchantId} : {};
    //#region Customer List Table
    $scope.dtOptions = {
        id: 'dtCustomerList',
        apiUrl: Emix.Api.Application.merchant.getMerchantCustomers,
        apiParams: params,
        saveState: false,
        allowFilter: true,
        additionalSortFieldName: 'id'
    }

    $scope.dtCols = [
        {
            name: 'maskCustomerUserName',
            title: 'common.userName',
            allowSorting: true,
            cellClass: 'text-center ltrdir',
            headerClass: 'text-center col-md-3'
        },
        {
            name: 'fullName',
            title: 'common.name',
            allowSorting: true,
            cellClass: 'text-center',
            headerClass: 'text-center col-md-5',
        },
        {
            name: 'createDate',
            title: 'common.createDate',
            allowSorting: true,
            cellClass: 'text-center',
            headerClass: 'text-center col-md-4',
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
    }
    $scope.dtEvents = {
    }
    //#endregion

}

angular.module('emixApp.controllers').controller('customerlist_index',
    ['$scope', 'httpServices', 'PagerService', '$rootScope', 'shareFunc', 'sharedInfo',
        customerList]);

angular.module('emixApp.controllers').controller('customerlist_index_modal',
    ['$scope', 'httpServices', 'PagerService', '$rootScope', 'shareFunc', 'sharedInfo', '$modalInstance', 'passObject',
        customerList
    ]);
