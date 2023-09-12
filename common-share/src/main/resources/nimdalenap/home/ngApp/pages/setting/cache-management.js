angular.module('emixApp.controllers').controller('cachemanagement_index',
    ['$scope', 'httpServices', '$translate',
        function ($scope, httpServices, $translate) {
        'use strict';

        //#region Properties
        //#endregion

        //#region Utils
        //#endregion

        //#region Methods
        $scope.clearAllCache = function () {
            NProgress.start();
            $.blockUI();
            httpServices.clearAllCache()
                .then(function (response) {
                    var data = response.data;
                    var t = data.returnObject;
                    toastr.success($translate.instant('common.successfullyActionMessage'));
                }, function (response) {
                    httpServices.handleError(response.data, response.status, response.headers, response.config);
                })
                .finally(function () {
                    NProgress.done();
                    $.unblockUI();
                });
        }
        //#endregion
    }]);

