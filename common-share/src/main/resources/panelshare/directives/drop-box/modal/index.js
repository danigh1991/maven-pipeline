angular.module('emixApp.controllers').controller('dropboxgridmodal_index',
    ['$scope', 'passObject', '$modalInstance', 'shareFunc', 'sharedInfo', '$translate', '$timeout',
        function ($scope, passObject, $modalInstance, shareFunc, sharedInfo, $translate, $timeout) {
            'use strict';

            //#region data table
            $scope.dtOptions = passObject.options;
            $scope.dtCols = passObject.dtCols || [];
            if($scope.dtCols.length > 0 && $scope.dtCols[$scope.dtCols.length - 1].name.length > 0) {
                $scope.dtCols.push({
                    name: '',
                    title: '',
                    allowSorting: false,
                    headerClass: 'text-center tdmini',
                    cellClass: 'tdcommand tdmini',
                });
            }

            $scope.dtData = [];
            $scope.dtAdditionalData = passObject.additionalData;
            $scope.dtEvents = passObject.events || {};
            /*$scope.dtEvents.onRowDblClick = function (item){
                $modalInstance.close(item);
            }*/
            $scope.dtEvents.onRowClick = function (item){
                $modalInstance.close(item);
            }
            //#endregion

            /*$timeout(function () {//Comment because call api twice
                $scope.dtEvents.dataBind();
            });*/

            $scope.closeModal = function () {
                $modalInstance.dismiss('cancel');
            }

        }]);
