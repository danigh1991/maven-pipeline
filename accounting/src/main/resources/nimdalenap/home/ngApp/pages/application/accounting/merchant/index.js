angular.module('emixApp.controllers').controller('merchantlist_index',
    ['$scope', 'httpServices', 'PagerService', '$rootScope', 'shareFunc', 'sharedInfo', '$translate',
        function ($scope, httpServices, PagerService, $rootScope, shareFunc, sharedInfo, $translate) {
            'use strict';

            $scope.addEdit = function (merchant) {
                var passObject = {
                };
                if(merchant){
                    passObject.id = merchant.id;
                }
                shareFunc.openModal(passObject, '/nimdalenap/home/ngApp/pages/application/accounting/merchant/add.html', 'addmerchant_index', 'lg',
                    function (result) {
                        $scope.dtEvents.dataBind();
                    }, function () {
                        //dismiss Dialog
                        if(sharedInfo.getNeedRefresh())
                            $scope.dtEvents.dataBind();
                    });
            }

            $scope.changeMerchantState = function (merchant){
                NProgress.start();
                $.blockUI();
                httpServices.changeMerchantState(merchant.id)
                    .then(function (response) {
                        var data = response.data;
                        sharedInfo.setNeedRefresh(true);
                        merchant.active = data.returnObject;
                        toastr.success($translate.instant('common.successfullySavedMessage'));
                    }, function (response) {
                        httpServices.handleError(response.data, response.status, response.headers, response.config);
                    })
                    .finally(function () {
                        NProgress.done();
                        $.unblockUI();
                    });
            }

            //#region ManualTransactionRequest Table
            $scope.dtOptions = {
                id: 'dtMerchantList',
                apiUrl: Emix.Api.Application.merchant.getAllMerchants,
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
                    name: 'userName',
                    title: 'common.userName',
                    allowSorting: true,
                    cellClass: 'text-center',
                    headerClass: 'text-center col-md-1'
                },
                {
                    name: 'name',
                    title: 'common.name',
                    allowSorting: true,
                    cellClass: 'text-center',
                    headerClass: 'text-center col-md-2',
                },
                {
                    name: 'merchantCategoryName',
                    keyName: 'merchantCategoryId',
                    title: 'common.type',
                    allowSorting: true,
                    cellClass: 'text-center',
                    headerClass: 'text-center col-md-1',
                    dataType: sharedInfo.dataType.list,
                    filterList: Emix.Api.Application.merchant.getAllMerchantCategories,
                    filterListKey: 'id',
                    filterListValue: 'name',
                },
                {
                    name: 'mobileNumber',
                    title: 'common.mobile',
                    allowSorting: true,
                    cellClass: 'text-center',
                    headerClass: 'text-center col-md-1',
                    dataType: sharedInfo.dataType.int,
                },
                {
                    name: 'cityName',
                    keyName: 'cityId',
                    title: 'common.city',
                    allowSorting: true,
                    cellClass: 'text-center',
                    headerClass: 'text-center col-md-1',
                    dataType: sharedInfo.dataType.list,
                    filterList: Emix.Api.Application.global.getCityList,
                    filterListKey: 'id',
                    filterListValue: 'name',
                },
                {
                    name: 'regionName',
                    title: 'common.region',
                    allowSorting: true,
                    cellClass: 'text-center',
                    headerClass: 'text-center col-md-1'
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
                    name: 'statusDesc',
                    keyName: 'active',
                    title: 'common.status',
                    allowSorting: true,
                    cellClass: 'text-center',
                    cellTemplate: ' <span class="label label-{{data.active ? \'success\' : \'danger\'}}">{{data.active ? (\'common.active\' | translate) : (\'common.inactive\' | translate)}}</span>',
                    headerClass: 'text-center col-md-1',
                    dataType: sharedInfo.dataType.list,
                    filterList: sharedInfo.getGlobalStatusList(),
                    filterListKey: 'value',
                    filterListValue: 'caption',
                },
                {
                    name: '',
                    title: '',
                    allowSorting: false,
                    headerClass: 'text-center',
                    cellClass: 'tdcommand',
                    cellTemplate: '<button class="btn btn-warning" ng-click="events.edit(data);"\n' +
                        '               button-text="common.edit">\n' +
                        '          </button>\n'+
                        '           <button class="btn" ng-click="events.changeState(data)"\n' +
                        '               ng-class="data.active ? \'btn-danger\' : \'btn-primary\'">\n' +
                        '               {{data.active ? (\'common.inactive\' | translate) : (\'common.active\' | translate)}}\n' +
                        '           </button>\n'+
                        '           <button class="btn btn-default" ng-click="events.customerList(data)"\n' +
                        '               button-text="common.customers">\n' +
                        '           </button>'
                }
            ];
            $scope.dtData = [];
            $scope.dtAdditionalData = {
                shopCurrency: $rootScope.shopCurrency,
                shopFractionSize: $rootScope.shopFractionSize,
            }
            $scope.dtEvents = {
                edit: function (data) {
                    $scope.addEdit(data);
                },
                changeState: function (data) {
                    $scope.changeMerchantState(data);
                },
                customerList: function (data) {
                    let passObject = {
                        merchantId: data.id
                    }
                    shareFunc.openModal(passObject, '/nimdalenap/home/ngApp/pages/application/accounting/merchant/customer/index.html', 'customerlist_index_modal', 'x-lg',
                        function (result) {
                        }, function () {
                        }, 'start');
                }
            }
            //#endregion

        }]);
