angular.module('emixApp.controllers').controller('refund_index',
    ['$scope', 'httpServices', 'PagerService', '$filter', 'shareFunc', 'sharedInfo', '$rootScope', function ($scope, httpServices, PagerService, $filter, shareFunc, sharedInfo, $rootScope) {
        'use strict';

        $scope.addRefund = Emix.Pages.refundAdd;

        function getAllRequestRefundMonies() {
            NProgress.start();
            $.blockUI();

            httpServices.getAllRequestRefundMonies(0, 99)
                .then(function (response) {
                    var data = response.data;
                }, function (response) {
                    httpServices.handleError(response.data, response.status, response.headers, response.config);
                })
                .finally(function () {
                    NProgress.done();
                    $.unblockUI();
                });
        }

        getAllRequestRefundMonies();

        //#region refund list table
        $scope.dtOptions = {
            id: 'dtRefundList',
            apiUrl: Emix.Api.Application.refund.getAllRequestRefundMonies,
            apiParams: {},
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
                headerClass: 'text-center tdmini',
                dataType: sharedInfo.dataType.int,
            },
            {
                name: 'reqUserName',
                title: 'accounting.applicant',
                allowSorting: true,
                cellClass: 'text-center',
                headerClass: 'text-center col-md-2'
            },
            {
                name: 'reqDesc',
                title: 'common.description',
                allowSorting: true,
                cellClass: 'text-center',
                headerClass: 'text-center col-md-2',
            },
            {
                name: 'reqAmount',
                title: 'common.amount',
                allowSorting: true,
                cellClass: 'text-center',
                headerClass: 'text-center col-md-2',
                dataType: sharedInfo.dataType.money,
                cellTemplate: '<span>{{data.reqAmount | currency:"":additionalData.shopFractionSize}} <small>{{additionalData.shopCurrency}}</small></span>'
            },
            {
                name: 'createDate',
                title: 'common.saveDate',
                allowSorting: true,
                cellClass: 'text-center',
                headerClass: 'text-center col-md-2',
                dataType: sharedInfo.dataType.date,
            },
            {
                name: 'financeDestValue',
                title: 'destination',
                allowSorting: true,
                cellClass: 'text-center',
                cellTemplate: '<span>{{data.financeDestCaption}}: {{data.financeDestValue}}</span>',
                headerClass: 'text-center col-md-2'
            },
            {
                name: 'statusDesc',
                title: 'common.status',
                keyName: 'status',
                allowSorting: true,
                cellClass: 'text-center',
                headerClass: 'text-center col-md-2',
                dataType: sharedInfo.dataType.list,
                filterList: Emix.Api.Application.refund.getRefundStatuses,
                filterListKey: 'id',
                filterListValue: 'caption',
            },
            {
                name: '',
                title: '',
                allowSorting: false,
                headerClass: 'text-center',
                cellClass: 'tdcommand',
                cellTemplate: '<button class="btn btn-info" ng-click="events.detailRefund(data)" button-type="details"></button>'
            }
        ];
        $scope.dtData = [];
        $scope.dtAdditionalData = {
            shopCurrency: $rootScope.shopCurrency,
            shopFractionSize: $rootScope.shopFractionSize,
        }
        $scope.dtEvents = {
            detailRefund: function (data) {
                var passObject = {
                    id : data.id,
                    reqUserId : data.reqUserId
                };
                shareFunc.openModal(passObject, '/nimdalenap/home/ngApp/pages/application/accounting/refund/details.html', 'detailsrefund_index', null,
                    function (result) {
                        if(sharedInfo.getNeedRefresh())
                            $scope.dtEvents.dataBind();
                    }, function () {
                        //dismiss Dialog
                        if(sharedInfo.getNeedRefresh())
                            $scope.dtEvents.dataBind();
                    });
            }
        }
        //#endregion

    }]);
