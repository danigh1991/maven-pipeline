angular.module('emixApp.controllers').controller('refund_index',
    ['$scope', 'httpServices', 'PagerService', '$filter', '$modal', '$translate', function ($scope, httpServices, PagerService, $filter, $modal, $translate) {
        'use strict';

        $scope.pager = {};
        $scope.setPage = setPage;
        $scope.dummyItems;
        $scope.items;

        $scope.dummyItemstmp;

        $scope.addRefund = Emix.Pages.refundAdd;

        function getAllRequestRefundMonies() {
            NProgress.start();
            $.blockUI();

            httpServices.getAllRequestRefundMonies(0, 99)
            .then(function (response) {
                var data = response.data;
                 $scope.dummyItemstmp = $scope.dummyItems = data.returnObject;
                 $scope.setPage(1);

             }, function (response) {
                 httpServices.handleError(response.data, response.status, response.headers, response.config);
            })
            .finally(function () {
                NProgress.done();
                $.unblockUI();                
            });
        }
        getAllRequestRefundMonies();

        /*$scope.params={};
        function getMyMainAccountInfo() {
            NProgress.start();
            $.blockUI();

            httpServices.getMyMainAccountInfo()
            .then(function (response) {
                var data = response.data;
                 $scope.balance = data.returnObject.balance;
                 $scope.block = data.returnObject.block;

                 $scope.params.accounts = [data.returnObject];
             }, function (response) {
                 httpServices.handleError(response.data, response.status, response.headers, response.config);
            })
            .finally(function () {
                //NProgress.done();
                //$.unblockUI();
                getAllRequestRefundMonies();
            });
        }
        getMyMainAccountInfo();*/

        $scope.cancelRefund = function(element, id){
            NProgress.start();
            $.blockUI();

            httpServices.cancelRequestRefundMoney(id)
                .then(function (response) {
                    var data = response.data;
                    toastr.success($translate.instant('accounting.refundCancled'));
                    getMyMainAccountInfo();
                }, function (response) {
                    httpServices.handleError(response.data, response.status, response.headers, response.config);
                })
                .finally(function () {
                    NProgress.done();
                    $.unblockUI();
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
