angular.module('emixApp.controllers').controller('bankinfo_index',
    ['$scope', 'httpServices', '$translate', 'sharedInfo', function ($scope, httpServices, $translate, sharedInfo) {
        'use strict';

        function getMyBankAccountInfo() {

            httpServices.getMyBankAccountInfo()
                .then(function (response) {
                    var data = response.data;

                    $scope.myBankAccountInfo = data.returnObject;
                }, function (response) {
                    httpServices.handleError(response.data, response.status, response.headers, response.config);
                })
                .finally(function () {
                    NProgress.done();
                    $.unblockUI();
                });
        }

        $scope.params={};
        function getMyMainAccountInfo() {
            NProgress.start();
            $.blockUI();

            httpServices.getMyMainAccountInfo()
            .then(function (response) {
                var data = response.data;
                 $scope.result = data.returnObject;
                 /*if ($scope.result.bank)
                     $scope.selectedBank = $scope.result.bank.id;*/
                 $scope.params.accounts = [$scope.result];

                getMyBankAccountInfo();

                 setTimeout(function () {
                     $('[data-toggle="tooltip"]').tooltip();
                 }, 0);
             }, function (response) {
                 httpServices.handleError(response.data, response.status, response.headers, response.config);
                NProgress.done();
                $.unblockUI();
            })
            .finally(function () {

            });
        }
        $scope.getBankList = function () {
            /*httpServices.geBankAllowAccountIntro()
             .success(function (data, status, headers, config) {
                 $scope.banks = data.returnObject;
                 if($scope.banks.length > 0)
                    $scope.selectedBank = $scope.banks[0].id;

                 getMyAccountInfo();
             })
            .error(function (data, status, headers, config) {
                httpServices.handleError(data, status, headers, config);
                NProgress.done();
                $.unblockUI();
            })
            .finally(function () {

            });*/
            getMyMainAccountInfo();
        }

        //#region Finance Config
        $scope.financeConfig = sharedInfo.getFinanceConfig();

        $scope.getActiveFinanceDestNumbers = function () {
            NProgress.start();
            $.blockUI();
            httpServices.getActiveFinanceDestNumbers()
                .then(function (response) {
                    var data = response.data;
                    $scope.activeFinanceDestNumbers = data.returnObject;
                    angular.forEach($scope.activeFinanceDestNumbers, function (activeFinanceDestNumber) {
                        switch (activeFinanceDestNumber.name) {
                            case 'account_number':
                                $scope.financeConfig.showAccountNumber = true;
                                break;
                            case 'cart_number':
                                $scope.financeConfig.showCartNumber = true;
                                if(activeFinanceDestNumber.mask)
                                    $scope.financeConfig.cartNumberMask = activeFinanceDestNumber.mask;
                                break;
                            case 'iban_number':
                                $scope.financeConfig.showIbanNumber = true;
                                if(activeFinanceDestNumber.mask)
                                    $scope.financeConfig.ibanNumberMask = activeFinanceDestNumber.mask;
                                break;
                            case 'swift_number':
                                $scope.financeConfig.showSwiftNumber = true;
                                if(activeFinanceDestNumber.mask)
                                    $scope.financeConfig.swiftNumberMask = activeFinanceDestNumber.mask;
                                break;
                            case 'paypal_account':
                                $scope.financeConfig.showPaypalAccount = true;
                                break;
                        }
                    })
                    $scope.getBankList();
                }, function (response) {
                    httpServices.handleError(response.data, response.status, response.headers, response.config);
                    NProgress.done();
                    $.unblockUI();
                })
                .finally(function () {

                });
        }
        //#endregion

        $scope.getActiveFinanceDestNumbers();

        $scope.registerAccountInfo = function () {

            if (!$scope.myForm.$valid && ($scope.financeConfig.showAccountNumber || $scope.financeConfig.showPaypalAccount)) {
                $scope.showError = true;
                toastr.error($translate.instant('common.completeFormCarefully'));
                return;
            }

            var accountData = {
                //bankId: $scope.selectedBank,
                id: $scope.myBankAccountInfo.id,
                title: $scope.myBankAccountInfo.title,
                acountNumber: $scope.myBankAccountInfo.acount_number,
                cardNumber: $scope.myBankAccountInfo.card_number,
                ibanNumber: $scope.myBankAccountInfo.iban_number,
                swiftNumber: $scope.myBankAccountInfo.swift_number,
                paypalAccount: $scope.myBankAccountInfo.paypal_account,
                password: $scope.password
            };

            NProgress.start();
            $.blockUI();
            httpServices.editMyBankAccountInfo(accountData)
            .then(function (response) {
                var data = response.data;
                 toastr.success($translate.instant('common.successfullySavedMessage'));
             }, function (response) {
                 httpServices.handleError(response.data, response.status, response.headers, response.config);
            })
            .finally(function () {
                NProgress.done();
                $.unblockUI();
            });
        }
    }]);
