angular.module('emixApp.controllers').controller('configuration_index',
    ['$scope', 'httpServices', 'FileUploader', '$rootScope', 'sharedInfo', '$timeout', '$translate', '$routeParams',
        function ($scope, httpServices, FileUploader, $rootScope, sharedInfo, $timeout, $translate, $routeParams) {
            'use strict';

            //#region Properties
            $scope.siteThemeListUrl = Emix.Pages.siteThemeList;
            $scope.currentConfigCat = null;
            $scope.sysName = $routeParams.sysName;
            $scope.isThemeConfig = $scope.sysName === 'theme_config';
            //#endregion

            //#region Utils
            $scope.rtlChar = /[\u0590-\u083F]|[\u08A0-\u08FF]|[\uFB1D-\uFDFF]|[\uFE70-\uFEFF]/mg;
            $scope.colapsPanel = function (configCat) {
                configCat.closePanel = !configCat.closePanel;
                $scope.currentConfigCat = configCat;
            }
            $scope.charControlType = function (config) {
                var fileControlConfigNames = 'logo,mobileLazyDefaultImage,webLazyDefaultImage,favIcon,';
                var mirrorControlConfigNames = 'themeStyle,';
                if (fileControlConfigNames.includes(config.name + ','))
                    return 'file';
                if (mirrorControlConfigNames.includes(config.name + ','))
                    return 'mirror';
                return 'textarea';
            }
            //#endregion

            //#region Methods
            $scope.getActiveSiteThemes = function () {
                NProgress.start();
                $.blockUI();
                httpServices.getActiveSiteThemes()
                    .then(function (response) {
                        var data = response.data;
                        $scope.activeSiteThemes = data.returnObject;
                        $scope.getConfigsTypeBySysName();
                    }, function (response) {
                        httpServices.handleError(response.data, response.status, response.headers, response.config);
                        NProgress.done();
                        $.unblockUI();
                    })
                    .finally(function () {

                    });
            }

            $scope.getConfigsTypeBySysName = function () {

                /*NProgress.start();
                $.blockUI();*/
                httpServices.getConfigsTypeBySysName($scope.sysName)
                    .then(function (response) {
                        var data = response.data;
                        $scope.result = data.returnObject;
                        $scope.currentConfigCat = $scope.result && $scope.result.length > 0 ? $scope.result[0] : null;
                    }, function (response) {
                        httpServices.handleError(response.data, response.status, response.headers, response.config);
                    })
                    .finally(function () {
                        NProgress.done();
                        $.unblockUI();
                    });
            }

            if ($scope.isThemeConfig)
                $scope.getActiveSiteThemes();
            else
                $scope.getConfigsTypeBySysName();

            $scope.editConfiguration = function (config, configCat) {

                $scope.currentConfigCat = configCat;

                if ((config.numValue != null && config.numValue.length == 0) ||
                    (config.chrValue != null && config.chrValue.length == 0)) {
                    toastr.error("مقدار وارد شده معتبر نمی باشد.");
                    return;
                }
                var cConfiguration = {
                    name: config.name,
                    chrVal: config.chrValue,
                    numVal: config.uiComponent === 'boolean' ? (config.numValue == true ? 1 : 0) : config.numValue,
                    active: config.active,
                    desc: config.desc
                }

                NProgress.start();
                $.blockUI();
                httpServices.editConfiguration(cConfiguration)
                    .then(function (response) {
                        var data = response.data;
                        config = data.returnObject;

                        var find = false;
                        angular.forEach($scope.currentConfigCat.configurations, function (item) {
                            if (!find && item.name === config.name) {
                                item.tmpNumValue = item.numValue = config.uiComponent === 'boolean' ? !!config.numValue : config.numValue;
                                item.tmpChrValue = item.chrValue = config.chrValue;
                                find = true;
                            }
                        });
                        //$scope.getAllConfigs();
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

