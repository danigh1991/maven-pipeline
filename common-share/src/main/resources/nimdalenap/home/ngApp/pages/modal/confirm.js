angular.module('emixApp.controllers').controller('confirmmodal_index',
    ['$scope', 'httpServices', '$modalInstance', 'passObject', '$sce', '$translate', function ($scope, httpServices, $modalInstance, passObject, $sce, $translate) {
        'use strict';

        $scope.title = passObject.title;
        $scope.message = $sce.trustAsHtml(passObject.message);
        $scope.displayConfirmButton = passObject.displayConfirmButton !== false;
        $scope.confirmText = passObject.confirmText ? passObject.confirmText : $translate.instant('common.verify');

        //$(document).ready(function () {
        //    $('#messageplaceholder').html($scope.message);
        //});
        //setTimeout(function () {
        //    bindPrintEvent();
        //}, 2000);
        setTimeout(function () {
            if (passObject.actionToCall)
                passObject.actionToCall();
        }, 2000);

        $scope.confirm = function () {
            $modalInstance.close(true);
        };
        $scope.cancel = function () {
            $modalInstance.dismiss('cancel');
        };

    }]);
