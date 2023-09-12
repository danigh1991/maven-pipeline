

angular.module('emixApp.controllers').controller('appAsideLte', ['$scope', '$location', '$modal', 'sharedInfo', '$rootScope', 'httpServices', '$route', 'authService',
function ($scope, $location, $modal, sharedInfo, $rootScope, httpServices, $route, authService) {
    'use strict';

    $scope.isLogin = authService.isLoggedIn();//$.cookie('accessToken') !== undefined;

    $scope.isMenuActive = function (element, path) {
        return ('#!' + $location.path()).substring(0, path.length) === path;
    };

    $scope.checkAndRedirect = function (element, path) {
        if (('#!' + $location.path()).substring(0, path.length) === path)
            $route.reload();
        else
            httpServices.redirect(path);
    };

    $scope.showModal = function (menuEntry,e) {
        if (menuEntry.isModal) {
            e.preventDefault();
        }

        setTimeout(fnCloseMenu, 100);
    }

    $scope.expandMenuItem = function (menuEntry) {

        if (menuEntry.isModal) {
            setTimeout(fnCloseMenu, 100);
        }
        else if (menuEntry.isGroup) {
            menuEntry.isExpanded = !menuEntry.isExpanded; //expanded if clicked
            setTimeout(fnFixContent, 100);
        }
        else
            setTimeout(fnCloseMenu, 100);
    };

    var fnFixContent = function () {
        $('.content-wrapper').css('min-height', $('.sidebar').height() + ' !important');
    }

    var fnCloseMenu = function () {
        if ($(document).width() < 748)
            $('.sidebar-toggle').click();
    }

    $rootScope.$on("CallUpdateInfo", function () {
        $scope.updateInfo();
    });

    $rootScope.$on("CallUpdateMenu", function () {
        $scope.updateMenu();
    });

    $scope.updateInfo = function () {
        //if (jQuery.cookie('accessToken') !== undefined) {
        $scope.userInfo = sharedInfo.getUserData();
        $scope.userId = sharedInfo.getUserId();

        $scope.userPic = Emix.Api.Account.image + sharedInfo.getUserLogoPath();
        //}
    }
    $scope.updateInfo();

    $scope.menuEntries = [];

    $scope.updateMenu = function () {

        $scope.menuEntries.length == 0 && httpServices.isUserLogin(function (isLogin) {
            if (isLogin) {
                NProgress.start();
                $.blockUI();
                httpServices.getPanelMenu()
                .then(function (response) {
                    var data = response.data;
                    $scope.menuEntries = data.returnObject;
                }, function (response) {
                    httpServices.handleError(response.data, response.status, response.headers, response.config);
                })
                .finally(function () {
                    NProgress.done();
                    $.unblockUI();
                });
            }
        });
    }
    $scope.updateMenu();

    //#region Modal
    //#endregion

}]);
