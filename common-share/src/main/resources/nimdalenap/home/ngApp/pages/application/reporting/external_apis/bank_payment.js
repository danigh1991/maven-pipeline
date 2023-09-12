angular.module('emixApp.controllers').controller('bankpaymentcalllist_index',
    ['$scope', 'httpServices', 'PagerService', '$rootScope', 'shareFunc', 'sharedInfo', '$translate',
        function ($scope, httpServices, PagerService, $rootScope, shareFunc, sharedInfo, $translate) {
            'use strict';

            //#region external api list Table
            $scope.dtOptions = {
                id: 'dtBankPaymentCallList',
                apiUrl: Emix.Api.Application.bankPayment.getBankPaymentWrappers,
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
                    name: 'bankName',
                    keyName: 'bankId',
                    title: 'common.bank',
                    allowSorting: true,
                    cellClass: 'text-center',
                    headerClass: 'text-center col-md-2',
                    dataType: sharedInfo.dataType.list,
                    filterList: Emix.Api.Application.bankPayment.getAllBanks,
                    filterListKey: 'id',
                    filterListValue: 'name',
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
                    name: 'myReferenceNumber',
                    title: 'common.trackingCode',
                    allowSorting: true,
                    cellClass: 'text-center',
                    headerClass: 'text-center col-md-3',
                },
                /*{
                    name: 'bankReferenceNumber',
                    title: 'accounting.bankTrackingNumber',
                    allowSorting: true,
                    cellClass: 'text-center',
                    headerClass: 'text-center col-md-2',
                },{
                    name: 'bankResponseLastStatus',
                    title: 'common.lastStatus',
                    allowSorting: true,
                    cellClass: 'text-center',
                    headerClass: 'text-center col-md-1',
                },{
                    name: 'bankResponseStatusHist',
                    title: 'common.statusHistory',
                    allowSorting: true,
                    cellClass: 'text-center',
                    headerClass: 'text-center col-md-2',
                },*/
                {
                    name: 'statusDesc',
                    keyName: 'status',
                    title: 'common.status',
                    allowSorting: true,
                    cellClass: 'text-center',
                    headerClass: 'text-center col-md-2',
                    dataType: sharedInfo.dataType.list,
                    filterList: Emix.Api.Application.bankPayment.getBankPaymentStatuses,
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
                    cellTemplate: '<button class="btn btn-warning" ng-click="events.detail(data);"\n' +
                        '               button-text="common.details">\n' +
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
                    //#region report detial
                    var passObject = {};
                    passObject.rgOptions = {
                        label: 'common.details',
                    };

                    passObject.rgTemplate = $('#bankPaymentDeailsReport').html();

                    passObject.rgRows = [];
                    passObject.rgData = data;
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
                    //#endregion
                }
            }
            //#endregion

        }]);
