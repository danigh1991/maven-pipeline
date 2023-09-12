commentListChangeDates = function () {
    var currScope = angular.element(document.getElementById('fromDate')).scope();
    currScope.fromDate = document.getElementById('fromDate').value;
    currScope.toDate = document.getElementById('toDate').value;
    currScope.$apply();
}
angular.module('emixApp.controllers').controller('commentlist_index',
    ['$scope', 'httpServices', 'sharedInfo', 'PagerService', '$filter', function ($scope, httpServices, sharedInfo, PagerService, $filter) {
        'use strict';
        /*$scope.targetTypeList = [
            { id: 0, name: 'all' },
            { id: 1, name: 'discount' },
            { id: 9, name: 'product' }
        ];*/
        $scope.targetTypeList = httpServices.getTargetTypeList();
        $scope.selectedTargetType = $scope.targetTypeList[0];

        $scope.pager = {};
        $scope.setPage = setPage;
        $scope.dummyItems = '';
        $scope.items = '';
        $scope.dummyItemstmp = '';
        httpServices.getToken();
        $scope.getAllMyComments = function () {

            var rangeDate = {
                fromDate: $scope.fromDate || '',
                toDate : $scope.toDate || ''
            }
            NProgress.start();
            $.blockUI();
            httpServices.getAllMyComments($scope.selectedTargetType.id, rangeDate)
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
        $scope.getAllMyComments();

        //httpServices.getCSRfToken();

        $scope.search = function () {
            $scope.dummyItems = $scope.dummyItemstmp;
            $scope.pager.totalPages = 1;
            $scope.dummyItems = $filter('filter')($scope.dummyItems, $scope.bzquery);
            setPage(1);
        }

        //#region Pager
        function setPage(page) {
            //if (page < 1 || page > $scope.pager.totalPages) {
            //    return;
            //}

            // get pager object from service
            $scope.pager = PagerService.GetPager($scope.dummyItems.length, page, 5);

            // get current page of items
            $scope.items = $scope.dummyItems.slice($scope.pager.startIndex, $scope.pager.endIndex + 1);
        }
        //#endregion

    }]);
