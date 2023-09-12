angular.module('emixApp.controllers').controller('activitylist_index',
    ['$scope', 'httpServices', 'PagerService', '$rootScope', 'shareFunc', 'sharedInfo', '$translate',
        function ($scope, httpServices, PagerService, $rootScope, shareFunc, sharedInfo, $translate) {
            'use strict';

            $scope.query = '';

            $scope.panelType = '0';
            $scope.panelTypeFilter = function (item) {
                var panelType = parseInt($scope.panelType, 10);
                return panelType === 0 || item.panelType === panelType || (panelType === 3 && item.panelType !== 1 && item.panelType !== 2);
            };

            function getAllActivities() {

                NProgress.start();
                $.blockUI();

                httpServices.getAllActivities()
                    .then(function (response) {
                        var data = response.data;
                        $scope.activities = data.returnObject;
                        initTree($scope.activities, null);
                    }, function (response) {
                        httpServices.handleError(response.data, response.status, response.headers, response.config);
                    })
                    .finally(function () {
                        NProgress.done();
                        $.unblockUI();
                    });
            }

            getAllActivities();

            $scope.editActivity = function (activityId) {
                var passObject = {activityId: activityId};
                shareFunc.openModal(passObject, '/nimdalenap/home/ngApp/pages/account/activity/edit.html', 'editactivity_index', null,
                    function (result) {
                        if (sharedInfo.getNeedRefresh())
                            getAllActivities();
                    }, function () {
                        //dismiss Dialog
                        if (sharedInfo.getNeedRefresh())
                            getAllActivities();
                    });
            }

            //#region Tree
            function initTree(tree, parentNode) {
                function processNode(node, mparent) {
                    node.parent = mparent;
                    angular.forEach(node.menuEntries, function (child) {
                        processNode(child, node);
                    });
                }

                if (!parentNode) parentNode = null;
                else parentNode.menuEntries = tree;
                angular.forEach(tree, function (node) {
                    processNode(node, parentNode ? parentNode : null);
                });
            }

            $scope.setExpand = function (data) {
                data.expand = !data.expand;
            }

            $scope.initNode = function (data) {
                data.expand = data.expand || false;
                data.hasAnyChild = data.menuEntries && data.menuEntries.length > 0;
            }
            //#endregion
        }]);
