angular.module('emixApp.controllers').controller('editrole_index',
    ['$scope', 'httpServices', 'passObject', '$rootScope', '$filter', 'shareFunc', 'sharedInfo', '$modalInstance', '$translate',
        function ($scope, httpServices, passObject, $rootScope, $filter, shareFunc, sharedInfo, $modalInstance, $translate) {
            'use strict';

            $scope.showError = false;
            $scope.showBreadcrump = !passObject;
            $scope.query = '';

            $scope.roleTypeList = sharedInfo.getRoleTypeList();


            $scope.fileManagerActionObj = { selected: [] };
            $scope.getFileManagerActions = function () {
                NProgress.start();
                $.blockUI();
                httpServices.getFileManagerActions()
                    .then(function (response) {
                        var data = response.data;
                        $scope.fileManagerActions = data.returnObject;
                        $scope.getAllActivities();
                    }, function (response) {
                        httpServices.handleError(response.data, response.status, response.headers, response.config);
                        NProgress.done();
                        $.unblockUI();
                    })
                    .finally(function () {

                    });
            }
            $scope.getFileManagerActions();

            $scope.getAllActivities = function () {

                httpServices.getAllActivities()
                    .then(function (response) {
                        var data = response.data;
                        $scope.activities = data.returnObject;
                        /*$scope.activities = [{
                            id: 0,
                            menuEntries: data.returnObject,
                            text: 'دسترسی',
                            parent: null,
                            expand: true
                        }]*/
                        initForm();
                    }, function (response) {
                        httpServices.handleError(response.data, response.status, response.headers, response.config);
                        NProgress.done();
                        $.unblockUI();
                    })
                    .finally(function () {

                    });
            }

            $scope.getRoleInfo = function () {

                httpServices.getRoleInfo(passObject.roleId)
                    .then(function (response) {
                        var data = response.data;
                        $scope.result = {
                            id: data.returnObject.id,
                            name: data.returnObject.roleName,
                            description: data.returnObject.description,
                            defaultRole: data.returnObject.defaultRole,
                            active: data.returnObject.active,
                            roleType: data.returnObject.roleType,
                            fileManagerActions: data.returnObject.fileManagerActions.map(function (value){ return value.id}),
                            activities: data.returnObject.activities.map(function (value){ return value.id})
                        }
                    }, function (response) {
                        httpServices.handleError(response.data, response.status, response.headers, response.config);
                    })
                    .finally(function () {
                        NProgress.done();
                        $.unblockUI();
                    });
            }

            var initForm = function () {
                initTree($scope.activities, null);
                if (passObject.roleId > 0) {
                    $scope.editMode = true;
                    $scope.getRoleInfo();
                } else {
                    $scope.editMode = false;
                    $scope.result = {
                        name: '',
                        description: '',
                        defaultRole: false,
                        active: true,
                        roleType: $scope.roleTypeList[0].id,
                        fileManagerActions: [],
                        activities: []
                    }
                    NProgress.done();
                    $.unblockUI();
                }
            }

            $scope.ok = function () {
                if($scope.editMode)
                    $scope.editRole();
                else
                    $scope.createRole();
            }

            $scope.editRole = function () {

                $scope.showError = true;
                if (!$scope.myForm.$valid) return;
                NProgress.start();
                $.blockUI();

                httpServices.editRole($scope.result)
                    .then(function (response) {
                        var data = response.data;
                        toastr.success($translate.instant('common.successfullySavedMessage'));
                        sharedInfo.setNeedRefresh(true);
                        setTimeout($scope.closeModal(), 1000);
                    }, function (response) {
                        httpServices.handleError(response.data, response.status, response.headers, response.config);
                    })
                    .finally(function () {
                        NProgress.done();
                        $.unblockUI();
                    });
            }

            $scope.createRole = function () {

                $scope.showError = true;
                if (!$scope.myForm.$valid) return;
                NProgress.start();
                $.blockUI();

                httpServices.createRole($scope.result)
                    .then(function (response) {
                        var data = response.data;
                        toastr.success($translate.instant('common.successfullySavedMessage'));
                        sharedInfo.setNeedRefresh(true);
                        setTimeout($scope.closeModal(), 1000);
                    }, function (response) {
                        httpServices.handleError(response.data, response.status, response.headers, response.config);
                    })
                    .finally(function () {
                        NProgress.done();
                        $.unblockUI();
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

            var updateActivities = function (data) {
                var index = $scope.result.activities.indexOf(data.id);
                if (data.selected) {
                    if(index === -1)
                        $scope.result.activities.push(data.id);
                } else {
                    if (index > -1) {
                        $scope.result.activities.splice(index, 1);
                    }
                }
            }

            $scope.changeAccess = function (data, checked) {
                data.selected = checked;
                updateActivities(data);
                changeChildAccess(data, checked);
                changeParentAccess(data);
            }
            var changeChildAccess = function (data, checked) {
                for (var i = 0; i < data.menuEntries.length; i++ ) {
                    data.menuEntries[i].selected = checked;
                    updateActivities(data.menuEntries[i]);
                    changeChildAccess(data.menuEntries[i], checked);
                }
            }
            var changeParentAccess = function (data) {
                var parent = data.parent;
                if (parent && parent.id > 0){
                    parent.selected = false;
                    for (var i = 0; i < parent.menuEntries.length; i++ ) {
                        parent.selected = parent.selected || parent.menuEntries[i].selected;
                    }
                    updateActivities(parent);
                    changeParentAccess(parent);
                }
            }

            $scope.setExpand = function (data) {
                data.expand = !data.expand;
            }

            $scope.initNode = function (data) {
                data.expand = data.expand || false;
                data.selected = $scope.result.activities.includes(data.id);
                data.hasAnyChild = data.menuEntries && data.menuEntries.length > 0;
            }
            //#endregion

            $scope.closeModal = function () {
                $modalInstance.dismiss('cancel');
            }

        }]);
