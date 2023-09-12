

angular.module('emixApp.controllers').controller('appAsideLte', ['$scope', '$window', '$location', 'sharedInfo', '$rootScope', 'httpServices', 'authService',
    function ($scope, $window, $location, sharedInfo, $rootScope, httpServices, authService) {
    'use strict';

    $scope.isLogin = authService.isLoggedIn();//$.cookie('accessToken') !== undefined;

    $scope.isMenuActive = function (element, path) {
        return ('#!' + $location.path()).substring(0, path.length) === path;
    };

    $scope.checkAndRedirect = function (path) {
        if (('#!' + $location.path()).substring(0, path.length) === path)
            $route.reload();
        else
            httpServices.redirect(path);
    };

    $scope.expandMenuItem = function (menuEntry) {
        if (menuEntry.isGroup) {
            menuEntry.isExpanded = !menuEntry.isExpanded; //expanded if clicked
            setTimeout(fnFixContent, 100);
        }
        else
            setTimeout(fnCloseMenu, 100);
    };

    var fnCloseMenu = function () {
        if ($(document).width() < 748)
            $('.sidebar-toggle').click();
    }

    var fnFixContent = function () {
        $('.content-wrapper').css('min-height', $('.sidebar').height() + ' !important');
    }


    $rootScope.$on("CallUpdateInfo", function () {
        $scope.updateInfo();
    });

    $rootScope.$on("CallUpdateMenu", function () {
        $scope.menuEntries = [];
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
                //Comment Because Close Loader Before End Page Loader
                /*NProgress.start();
                $.blockUI();*/
                httpServices.getAdminPanelMenu()
                .then(function (response) {
                    var data = response.data;
                     $scope.menuEntries = data.returnObject;
                }, function (response) {
                    httpServices.handleError(response.data, response.status, response.headers, response.config);
                })
                .finally(function () {
                    /*NProgress.done();
                    $.unblockUI();*/
                });
            }
        });
    }
    $scope.updateMenu();

}]);

//#region MultiLnguage
$('ul.lngmnu li:not(.activelang)').unbind('click').bind('click', function () {
    var $this = $(this);
    var hashtagVal = location.hash;
    location.href = $this.data('url') + hashtagVal;
})
//#endregion