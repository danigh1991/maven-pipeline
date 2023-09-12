angular.module('emixApp.controllers').controller('accountinfo_index',
    ['$scope', 'httpServices', '$rootScope', function ($scope, httpServices,$rootScope) {
        'use strict';

        function getMyMainAccountInfo() {
            NProgress.start();
            $.blockUI();

            httpServices.getMyMainAccountInfo()
                .then(function (response) {
                    var data = response.data;
                    $scope.result = data.returnObject;
                }, function (response) {
                    httpServices.handleError(response.data, response.status, response.headers, response.config);
                })
                .finally(function () {
                    NProgress.done();
                    $.unblockUI();
                });
        }
        getMyMainAccountInfo();
    }]);
