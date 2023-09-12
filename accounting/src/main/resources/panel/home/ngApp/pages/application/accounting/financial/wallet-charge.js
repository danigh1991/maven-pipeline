angular.module('emixApp.controllers').controller('wallet_charge_index',
    ['$scope', 'httpServices', 'sharedInfo', '$translate', 'passObject', function ($scope, httpServices, sharedInfo, $translate, passObject) {
        'use strict';

        $scope.bankGetways;// = sharedInfo.getBankGetways();
        $scope.params={};

        function getValidActiveBankGateways() {
            NProgress.start();
            $.blockUI();

            httpServices.getValidActiveBankGateways()
                .then(function (response) {
                    var data = response.data;
                    $scope.bankGetways = data.returnObject;
                    if($scope.bankGetways && $scope.bankGetways.length > 0)
                        $scope.selectedBank = $scope.bankGetways[0].id;
                    getAccountWrapperInfo()
                }, function (response) {
                    httpServices.handleError(response.data, response.status, response.headers, response.config);
                    NProgress.done();
                    $.unblockUI();
                })
                .finally(function () {
                });
        }
        getValidActiveBankGateways();

        function getAccountWrapperInfo() {
            /*NProgress.start();
            $.blockUI();*/

            httpServices.getAccountWrapperInfo(passObject.accountId)
            .then(function (response) {
                var data = response.data;
                 $scope.result = data.returnObject;
                 $scope.params.accounts = [$scope.result];

            }, function (response) {
                httpServices.handleError(response.data, response.status, response.headers, response.config);
            })
            .finally(function () {
                NProgress.done();
                $.unblockUI();
            });
        }

        $scope.chargeWallet = function () {

            $scope.showError = true;
            if (!$scope.myForm.$valid) {
                toastr.error($translate.instant('common.completeFormCarefully'));
                return;
            }

            var chargeWalletData = {
                accountId: passObject.accountId,
                gatewayId: $scope.selectedBank,
                amount: parseFloat($scope.amount)
            };;

            NProgress.start();
            $.blockUI();
            httpServices.chargeAccountAsBankPayment(chargeWalletData)
            .then(function (response) {
                var data = response.data;
                 httpServices.redirectToIpg(data.returnObject.bankInfo, data.returnObject.message, 'frmBankPayment');
            }, function (response) {
                httpServices.handleError(response.data, response.status, response.headers, response.config);
            })
            .finally(function () {
                NProgress.done();
                $.unblockUI();
            });
        }

    }]);
