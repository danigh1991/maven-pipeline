angular.module('emixApp.controllers').controller('accountingdashboard_index',
    ['$scope', 'httpServices', '$translate', '$filter', '$rootScope', function ($scope, httpServices, $translate, $filter, $rootScope) {
        'use strict';

        if (!(jQuery.cookie('accessToken') === undefined && $("body").attr("class") === 'skin-blue ng-scope')) {
            jQuery('.sidebar-toggle').show();
            //jQuery('.sidebar-toggle').click();
            jQuery('.navbar-custom-menu').show();
        }

        $scope.getAccountingDashboardWrapper = function () {
            NProgress.start();
            $.blockUI();

            httpServices.getAccountingDashboardWrapper()
                .then(function (response) {
                    var data = response.data;
                    $scope.result = data.returnObject;
                    $scope.setMoneyInOutChartData($scope.result.accountDashboardLabels, $scope.result.moneyInOut)
                    $scope.setRegisterMerchantCountChartData($scope.result.accountDashboardLabels, $scope.result.registerMerchantCount)
                    $scope.setRegisterUserCountChartData($scope.result.accountDashboardLabels, $scope.result.registerUserCount)
                    $scope.setOperationTypeSummeryChartData($scope.result.operationTypeSummeryLabels, $scope.result.operationTypeSummery)
                }, function (response) {
                    httpServices.handleError(response.data, response.status, response.headers, response.config);
                })
                .finally(function () {
                    NProgress.done();
                    $.unblockUI();
                });
        }
        $scope.getAccountingDashboardWrapper();

        $scope.setMoneyInOutChartData = function (labels, data) {
            $scope.moneyInOutChartsLabels = labels;
            $scope.moneyInOutChartsData = data;
        }
        $scope.setRegisterMerchantCountChartData = function (labels, data) {
            $scope.registerMerchantCountChartsLabels = labels;
            $scope.registerMerchantCountChartsData = data;
        }

        $scope.setRegisterUserCountChartData = function (labels, data) {
            $scope.registerUserCountChartsLabels = labels;
            $scope.registerUserCountChartsData = data;
        }

        $scope.setOperationTypeSummeryChartData = function (labels, data) {
            $scope.operationTypeSummeryChartsLabels = labels;
            $scope.operationTypeSummeryChartsData = data[0];
        }

        setTimeout(function () {
            $('[data-toggle="tooltip"]').tooltip();
            $('.collapse').collapse()
        }, 0);

        $scope.collapsClick = function (sender) {
            var $sender = $('[data-target="' + sender + '"]');
            var i = $sender.find('.fa');
            if ($sender.attr('aria-expanded') == "true") {
                i.removeClass('fa-minus');
                i.addClass('fa-plus');
            } else {
                i.removeClass('fa-plus');
                i.addClass('fa-minus');
            }
        }

        /*#region Chart*/
        $scope.chartColors = ['#00ADF9', '#803690', '#DCDCDC', '#46BFBD'
            , '#FDB45C', '#949FB1', '#4D5360', '#ff6384'
            , '#ff8e72'];

        $scope.colors = ['#45b7cd', '#ff6384', '#ff8e72'];
        $scope.moneyInOutOptions = {
            responsive: true,
            interaction: {
                mode: 'index',
                intersect: false,
            },
            plugins: {
                legend: {
                    display: true,
                },
                tooltip: {
                    callbacks: {
                        label: function(context) {
                            let label = $translate.instant(context.dataset.label || '');

                            if (label) {
                                label += ': ';
                            }
                            if (context.parsed.y !== null) {
                                let tmp = $filter('currency')(context.parsed.y, '', $rootScope.shopFractionSize)
                                label += tmp;
                            }
                            return label;
                        }
                    }
                },
            },
            scales: {
                y: {
                    //type: 'linear',
                    display: true,
                    position: 'left',
                }
            }
            /*scaleLabel: (label) => $filter('currency')(label.value),
            tooltipTemplate: (data) => `${data.label} ss : ${$filter('currency')(data.value)}`*/
        };
        $scope.moneyInOutDataset = [
            {
                label: 'واریز بانکی',//$translate.instant('accounting.moneyIn') + ' ',
                fill: true,
                fillColor: "rgba(60,141,188,0.9)",
                strokeColor: "rgba(60,141,188,0.8)",
                pointColor: "#00a65a",
                pointStrokeColor: "rgba(0,166,90,1)",
                pointHighlightFill: "#fff",
                pointHighlightStroke: "rgba(0,166,90,1)",
                borderColor: 'rgba(0,166,90, 1)',
                borderRadius: 5,
                backgroundColor: 'rgba(0,166,90,0.5)',
                pointBorderColor: 'rgba(0,166,90,0.7)',
                pointBackgroundColor: 'rgba(0,166,90,0.2)',
                pointBorderWidth: 1,
                type: 'line',
                //yAxisID: 'y',
                tension: .3
            },
            {
                label: 'برداشت بانکی',//$translate.instant('accounting.moneyOut') + ' ',
                fill: true,
                fillColor: "rgba(255,99,132,0.9)",
                strokeColor: "rgba(255,99,132,0.8)",
                pointColor: "#ff6384",
                pointStrokeColor: "rgba(255,99,132,1)",
                pointHighlightFill: "#fff",
                pointHighlightStroke: "rgba(255,99,132,1)",
                borderColor: 'rgba(255,99,132, 0.7)',
                borderRadius: 5,
                backgroundColor: 'rgba(255,99,132, 0.5)',
                pointBorderColor: 'rgba(255,99,132, 0.9)',
                pointBackgroundColor: 'rgba(255,99,132, 0.4)',
                pointBorderWidth: 1,
                type: 'line',
                //yAxisID: 'y1',
                tension: .3
            }
        ];

        $scope.registerMerchantCountOptions = {
            responsive: true,
            interaction: {
                mode: 'index',
                intersect: false,
            },
            stacked: false,
            plugins: {
                legend: {display: false},
                tooltip: {
                    callbacks: {
                        label: function (context) {
                            let label = $translate.instant(context.dataset.label || '');

                            if (label) {
                                label += ': ';
                            }
                            if (context.parsed.y !== null) {
                                let tmp = $filter('currency')(context.parsed.y, '', $rootScope.shopFractionSize)
                                label += tmp;
                            }
                            return label;
                        }
                    }
                },
            }
        };

        $scope.registerMerchantCountDataset = [
            {
                label: $translate.instant('common.count') + ' ',
                fill: true,
                fillColor: "rgba(60,141,188,0.9)",
                strokeColor: "rgba(60,141,188,0.8)",
                pointColor: "#3b8bba",
                pointStrokeColor: "rgba(60,141,188,1)",
                pointHighlightFill: "#fff",
                pointHighlightStroke: "rgba(60,141,188,1)",
                borderColor: 'rgba(60, 141, 188, 0.7)',
                backgroundColor: 'rgba(44, 152, 214, 0.5)',
                pointBorderColor: 'rgba(37, 103, 142, 0.9)',
                pointBackgroundColor: 'rgba(60, 141, 188, 0.4)',
                pointBorderWidth: 1,
                type: 'line',
                tension: .3
            }
        ];

        $scope.registerUserCountOptions = {
            responsive: true,
            interaction: {
                mode: 'index',
                intersect: false,
            },
            stacked: false,
            plugins: {
                legend: {display: false},
                tooltip: {
                    callbacks: {
                        label: function (context) {
                            let label = $translate.instant(context.dataset.label || '');

                            if (label) {
                                label += ': ';
                            }
                            if (context.parsed.y !== null) {
                                let tmp = $filter('currency')(context.parsed.y, '', $rootScope.shopFractionSize)
                                label += tmp;
                            }
                            return label;
                        }
                    }
                },
            }
        };
        $scope.registerUserCountDataset = [
            {
                label: $translate.instant('common.count') + ' ',
                fill: true,
                fillColor: "rgba(253, 180, 92,0.9)",
                strokeColor: "rgba(253, 180, 92,0.8)",
                pointColor: "#FDB45C",
                pointStrokeColor: "rgba(253, 180, 92,1)",
                pointHighlightFill: "#fff",
                pointHighlightStroke: "rgba(253, 180, 92,1)",
                borderColor: 'rgba(253, 180, 92, 0.7)',
                backgroundColor: 'rgba(253, 180, 92, 0.5)',
                pointBorderColor: 'rgba(253, 180, 92, 0.9)',
                pointBackgroundColor: 'rgba(253, 180, 92, 0.4)',
                pointBorderWidth: 1,
                type: 'line',
                tension: .3
            }
        ];

        $scope.operationTypeSummeryOptions = {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    display: true,
                },
                tooltip: {
                    callbacks: {
                        label: function (context) {
                            let label = $translate.instant(context.label || '');

                            if (label) {
                                label += ': ';
                            }
                            if (context.raw !== null) {
                                let tmp = $filter('currency')(context.raw, '', $rootScope.shopFractionSize)
                                label += tmp;
                            }
                            return label;
                        }
                    }
                },
            }
        };

        /*#endregion*/

    }]);
