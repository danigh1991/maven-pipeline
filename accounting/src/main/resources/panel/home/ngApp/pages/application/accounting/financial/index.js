angular.module('emixApp.controllers').controller('financial_index',
    ['$scope', 'httpServices', 'PagerService', '$filter', '$modal', '$translate', function ($scope, httpServices, PagerService, $filter, $modal,$translate) {
        'use strict';

        $scope.pager = {};
        $scope.setPage = setPage;
        $scope.dummyItems = '';
        $scope.items = '';

        $scope.dummyItemstmp = '';

        function getAccountStatements(accountId, showProgress) {
            if(showProgress) {
                NProgress.start();
                $.blockUI();
            }

            httpServices.getAccountStatements(accountId, 0, 1000)
            .then(function (response) {
                var data = response.data;
                 $scope.dummyItemstmp = $scope.dummyItems = data.returnObject.result;
                 $scope.setPage(1);

             }, function (response) {
                 httpServices.handleError(response.data, response.status, response.headers, response.config);
            })
            .finally(function () {
                NProgress.done();
                $.unblockUI();                
            });
        }

        
        $scope.getRemain = function (credit, debit) {
            $scope.tmpBalance = credit ? ($scope.tmpBalance - credit) : debit ? ($scope.tmpBalance + debit) : 0;
            return $scope.tmpBalance;
        }

        $scope.params={};
        $scope.events={
            loadAccountTypeDetail: function (account) {
                getAccountStatements(account.id, true);
            }
        };
        function getMyAccounts() {
            NProgress.start();
            $.blockUI();

            httpServices.getMyAccounts()
            .then(function (response) {
                var data = response.data;
                 /*$scope.tmpBalance = $scope.balance = data.returnObject.balance;
                 $scope.block = data.returnObject.block;*/
                 $scope.params.accounts = data.returnObject;

                 getAccountStatements($scope.params.accounts[0].id, false);
             }, function (response) {
                 httpServices.handleError(response.data, response.status, response.headers, response.config);
                NProgress.done();
                $.unblockUI();
            })
            .finally(function () {
            });
        }
        getMyAccounts();
        
        function getOrderAsHtml(orderId, callback) {
            NProgress.start();
            $.blockUI();
            httpServices.getOrderAsHtml(orderId)
            .then(function (response) {
                var data = response.data;
                 callback(data.returnObject);
             }, function (response) {
                 httpServices.handleError(response.data, response.status, response.headers, response.config);
            })
            .finally(function () {
                NProgress.done();
                $.unblockUI();
            });
        }
        $scope.detailOrder = function (element, orderId) {
            var passObject;
            getOrderAsHtml(orderId, function (returnObject) {

                passObject = {
                    title: 'فاکتور',
                    message: returnObject.result,
                    confirmText: $translate.instant('common.print'),
                    displayConfirmButton: false,
                    actionToCall: function () {
                        bindPrintEvent();
                    }
                }
                var dialogInst = $modal.open({
                    templateUrl: '/nimdalenap/home/ngApp/pages/modal/confirm.html',
                    controller: 'confirmmodal_index',
                    size: 'x-lg',
                    backdrop: 'static',
                    keyboard: false,//true
                    resolve: {
                        passObject: function () {
                            return passObject;
                        }
                    }
                });
                setTimeout(function() {
                    $('#btnChangeAddressShipment,#btnCancleShipment').hide();
                },100);
                setTimeout(function() {
                    $('#btnChangeAddressShipment,#btnCancleShipment').hide();
                },300);
                dialogInst.result.then(function (result) {
                    if (result) {
                        bindPrintEvent();
                        $('#btnprint').click();
                    }

                }, function () {
                    //$log.info('Modal dismissed at: ' + new Date());
                });
            });
        }

        $scope.search = function () {
            $scope.dummyItems = $scope.dummyItemstmp;
            $scope.pager.totalPages = 1;
            $scope.dummyItems = $filter('filter')($scope.dummyItems, $scope.bzquery);
            setPage(1);
        }
        //#region Pager
        function setPage(page) {
            /*if (page < 1 || page > $scope.pager.totalPages) {
                return;
            }*/

            // get pager object from service
            $scope.pager = PagerService.GetPager($scope.dummyItems.length, page, 5);

            // get current page of items
            $scope.items = $scope.dummyItems.slice($scope.pager.startIndex, $scope.pager.endIndex + 1);
        }
        //#endregion
    }]);
