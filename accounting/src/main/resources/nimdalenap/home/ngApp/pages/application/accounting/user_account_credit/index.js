angular.module('emixApp.controllers').controller('accountcreditdetails_index',
    ['$scope', 'httpServices', 'PagerService', '$rootScope', 'shareFunc', 'sharedInfo', '$translate', '$routeParams',
        function ($scope, httpServices, PagerService, $rootScope, shareFunc, sharedInfo, $translate, $routeParams) {
            'use strict';

            $scope.accountId = $routeParams.accountId;
            $scope.accountTypeId = $routeParams.accountTypeId;

            $scope.getAccountCreditDetails = function () {

                NProgress.start();
                $.blockUI();

                httpServices.getAccountCreditDetails($scope.accountId)
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

            $scope.getAccountCreditDetails();

            $scope.addEdit = function (accountCredit) {
                var passObject = {
                    accountId: $scope.accountId,
                    accountTypeId: $scope.accountTypeId
                };
                if(accountCredit){
                    passObject.id = accountCredit.id;
                }
                shareFunc.openModal(passObject, '/nimdalenap/home/ngApp/pages/application/accounting/user_account_credit/add.html', 'addaccountcreditdetail_index', 'x-lg',
                    function (result) {
                        $scope.getAccountCreditDetails();
                    }, function () {
                        //dismiss Dialog
                        if(sharedInfo.getNeedRefresh())
                            $scope.getAccountCreditDetails();
                    });
            }

            $scope.changeStateAccountCreditDetail = function (data) {
                NProgress.start();
                $.blockUI();
                httpServices.changeStateAccountCreditDetail(data.id)
                    .then(function (response) {
                        data.active = response.data.returnObject;
                        var message = data.active ? $translate.instant('common.successfullyActivedElement', {'element' : ($translate.instant('accounting.accountCreditDetails'))}) : $translate.instant('common.successfullyInactivedElement', {'element' : ($translate.instant('accounting.accountCreditDetail'))});
                        toastr.success(message);
                    }, function (response) {
                        httpServices.handleError(response.data, response.status, response.headers, response.config);
                    })
                    .finally(function () {
                        NProgress.done();
                        $.unblockUI();
                    });
            }

            //#region OperationType Table
            $scope.dtOptions = {
                id: 'dtAccountCreditDetails',
                saveState: true,
                allowFilter: false,
                additionalSortFieldName: 'id'
            }

            $scope.dtCols = [
                {
                    name: 'id',
                    title: 'common.id',
                    allowSorting: true,
                    cellClass: 'text-center',
                    headerClass: 'text-center tdmini'
                },
                {
                    name: 'title',
                    title: 'common.title',
                    allowSorting: true,
                    cellClass: 'text-center',
                    headerClass: 'text-center col-md-3'
                },
                {
                    name: 'creditAmount',
                    title: 'accounting.creditAmount',
                    allowSorting: true,
                    cellClass: 'text-center',
                    headerClass: 'text-center col-md-2',
                    cellTemplate: '<span class="prcblk">{{data.creditAmount | currency:"":additionalData.shopFractionSize}} <small>{{additionalData.shopCurrency}}</small></span>'
                },
                {
                    name: 'creditAmountPerUser',
                    title: 'accounting.creditAmountPerUser',
                    allowSorting: true,
                    cellClass: 'text-center',
                    headerClass: 'text-center col-md-2',
                    cellTemplate: '<span class="prcblk">{{data.creditAmountPerUser | currency:"":additionalData.shopFractionSize}} <small>{{additionalData.shopCurrency}}</small></span>'
                },
                {
                    name: 'statusDesc',
                    title: 'common.status',
                    allowSorting: true,
                    cellClass: 'text-center',
                    headerClass: 'text-center col-md-3',
                    cellTemplate: '<span class="label label-{{data.active ? \'success\' : \'danger\'}}">{{data.active ? (\'common.active\' | translate) : (\'common.inactive\' | translate)}}</span>'
                },

                {
                    name: '',
                    title: '',
                    allowSorting: false,
                    headerClass: 'text-center',
                    cellClass: 'tdcommand',
                    cellTemplate:
                        '  <div class="btn-group">\n' +
                        '        <button type="button" class="btn btn-info btn-rounded dropdown-toggle" data-toggle="dropdown" aria-expanded="false">{{\'common.action\' | translate}} <span class="caret"></span></button>\n' +
                        '        <div class="dropdown-menu panel-dropdown action-list" role="menu">\n' +
                        '            <button class="btn btn-warning" ng-click="events.edit(data);"\n' +
                        '               button-type="edit">\n' +
                        '            </button>\n' +
                        '            <a class="btn btn-warning" \n' +
                        '               ng-href="{{additionalData.accountCreditDetailPolicy + \'/\' + data.id}}" button-text="accounting.assignToUsers"></a>\n' +
                        '            <a class="btn btn-warning" \n' +
                        '               ng-href="{{additionalData.accountCreditMerchantLimit + \'/\' + data.id}}" button-text="common.merchants"></a>\n' +
                        '        </div>\n' +
                        '    </div>\n' +
                        '    <button class="btn" style="font-weight:100;" id="changeState" ng-click="events.changeState(data)"\n' +
                        '        ng-class="data.active ? \'btn-danger\' : \'btn-primary\'">\n' +
                        '    {{data.active ? (\'common.inactive\' | translate) : (\'common.active\' | translate)}}</button>'

                }
            ];
            $scope.dtData = [];
            $scope.dtAdditionalData = {
                shopCurrency: $rootScope.shopCurrency,
                shopFractionSize: $rootScope.shopFractionSize,
                accountCreditDetailPolicy: Emix.Pages.accountCreditDetailPolicy,
                accountCreditMerchantLimit: Emix.Pages.accountCreditMerchantLimit
            }
            $scope.dtEvents = {
                edit: function (accountCredit) {
                    $scope.addEdit(accountCredit);
                },
                changeState: function (data) {
                    $scope.changeStateAccountCreditDetail(data);
                }
            }
            //#endregion

        }]);
