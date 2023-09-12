angular.module('emixApp.controllers').controller('contactuslist_index',
    ['$scope', 'httpServices', 'sharedInfo', 'PagerService', '$filter', function ($scope, httpServices, sharedInfo, PagerService, $filter) {
        'use strict';

        $scope.pager = {};
        $scope.setPage = setPage;
        $scope.dummyItems = '';
        $scope.items = '';
        $scope.dummyItemstmp = '';
        $scope.letterLimit = 100;

        $scope.typeList = [
            { id: 0, name: 'تمام موارد' },
            { id: 1, name: 'پاسخ داده نشده' },
        ];
        $scope.selectedType = $scope.typeList[1];

        $scope.getAllContactusMessage = function () {
            NProgress.start();
            $.blockUI();

            httpServices.getContactUsList($scope.selectedType.id, 0, 1000)
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

        $scope.getAllContactusMessage();

        $scope.reply = function (_messageId) {
            httpServices.redirect(Emix.Pages.contactUsReply + '/' + _messageId);
        }

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
