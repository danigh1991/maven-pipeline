

angular.module('emixApp.controllers').controller('appHeaderLte', ['$scope', '$rootScope', 'httpServices', '$window', '$location', '$translate', 'sharedInfo', '$timeout', '$cookies', function ($scope, $rootScope, httpServices, $window, $location, $translate, sharedInfo, $timeout, $cookies) {
    'use strict';

    $scope.isLanguageActive = function (language) {
        return language === $translate.use();
    };

    /*$scope.changeLanguage = function (language) {
        //set cookie value according to translator plugin.
        //the probably methods are internal => possible issues
        /!*$translate.storage().set($translate.storageKey(), language);*!/

        $window.location.reload(); //refresh current page reloading correct culture
    };*/

    sharedInfo.setLocaleLang($cookies.get(siteConfig.langCookieName));

    $rootScope.$on("CallUpdateInfo", function () {
        $scope.updateInfo();
    });

    $scope.updateInfo = function () {
        //if (jQuery.cookie('accessToken') !== undefined) {
            $scope.userInfo = sharedInfo.getUserData();
            $scope.todaylabel = sharedInfo.getDate('longdate');//todaydate('longdate');
            $scope.userId = sharedInfo.getUserId();
            $scope.userPic = Emix.Api.Account.image + sharedInfo.getUserLogoPath();

        //    httpServices.getOrdersInfo()
        //             .success(function (data, status, headers, config) {
        //                 $scope.ordersInfo = data;
        //             })
        //            .error(function (data, status, headers, config) {
        //                httpServices.handleError(data, status, headers, config);
        //            })
        //            .finally(function () {

        //            });
        //}
    }
    $scope.updateInfo();
}]);
