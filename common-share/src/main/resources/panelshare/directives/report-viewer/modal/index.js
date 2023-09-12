angular.module('emixApp.controllers').controller('reportviewermodal_index',
    ['$scope', 'passObject', '$modalInstance', 'shareFunc', 'sharedInfo', '$translate', '$timeout', 'httpServices',
        function ($scope, passObject, $modalInstance, shareFunc, sharedInfo, $translate, $timeout, httpServices) {
            'use strict';

            //#region data table
            $scope.rgOptions = passObject.rgOptions;
            $scope.rgRows = passObject.rgRows || [];
            $scope.allowPrint = passObject.allowPrint === true;

            $scope.rgData = passObject.rgData || [];
            $scope.dtAdditionalData = passObject.additionalData;
            $scope.dtEvents = passObject.events || {};

            $scope.rgTemplate = passObject.rgTemplate;

            $scope.printReport = function () {
                shareFunc.printHtml('#rptMainSection', httpServices.getPanelType() === 'admin');
            }

            $scope.closeModal = function () {
                $modalInstance.dismiss('cancel');
            }

        }]);
